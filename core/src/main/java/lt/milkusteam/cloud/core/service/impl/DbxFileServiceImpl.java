package lt.milkusteam.cloud.core.service.impl;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.users.FullAccount;
import lt.milkusteam.cloud.core.dao.DbxTokenDao;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.model.Pair;
import lt.milkusteam.cloud.core.service.DbxFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by gediminas on 4/17/16.
 */
@Service
public class DbxFileServiceImpl implements DbxFileService {

    private static final long CHUNK_SIZE = 104857600;

    private static final Logger LOGGER = LoggerFactory.getLogger(DbxFileServiceImpl.class);

    private Map<String, DbxClientV2> dbxClients = new HashMap<>();

    private Map<String, String> dbxSpaceUsage = new HashMap<>();

    @Autowired
    private DbxTokenDao dbxTokenDao;

    @Override
    public Pair<List<FolderMetadata>, List<FileMetadata>> getMetadataPair(String username, String path) throws DbxException {
        LOGGER.info("getMetadataPair() username={} dirPath={}", username, path);
        Pair<List<FolderMetadata>, List<FileMetadata>> result =
                new Pair<>(new ArrayList<FolderMetadata>(), new ArrayList<FileMetadata>());
        try {
            List<FolderMetadata> folderList = result.getLeft();
            List<FileMetadata> fileList = result.getRight();
            ListFolderResult folderResult = dbxClients.get(username).files().listFolder(path);
            List<Metadata> temp = folderResult.getEntries();
            for (Metadata data : temp) {
                if (data instanceof FolderMetadata) {
                    folderList.add((FolderMetadata) data);
                } else {
                    fileList.add((FileMetadata) data);
                }
            }
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return result;
    }

    @Override
    public List<DeletedMetadata> getAllDeletedMetadata(String username) throws DbxException {
        LOGGER.info("getAllDeletedMetadata() username={}", username);
        List<DeletedMetadata> result = new ArrayList<>();
        try {
            List<Metadata> allMetadata = dbxClients.get(username)
                    .files()
                    .listFolderBuilder("")
                    .withRecursive(true)
                    .withIncludeDeleted(true)
                    .start()
                    .getEntries();
            for (Metadata data : allMetadata) {
                if (data instanceof DeletedMetadata) {
                    result.add(0, (DeletedMetadata) data);
                }
            }
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return result;
    }

    @Override
    public boolean containsClient(String username) {
        return dbxClients.containsKey(username);
    }

    @Override
    public void removeClient(String username) {
        LOGGER.info("removeClient() username={}", username);
        dbxClients.remove(username);
        dbxSpaceUsage.remove(username);
        LOGGER.info(username + " client removed.");
    }

    @Override
    public boolean addClient(String username) {
        LOGGER.info("addClient() username={}", username);
        DbxRequestConfig config = new DbxRequestConfig("DDD Cloud Manager", Locale.getDefault().toString());
        DbxToken accessToken = dbxTokenDao.findByUsername(username);
        if (accessToken != null) {
            DbxClientV2 client = new DbxClientV2(config, accessToken.getToken());
            dbxClients.put(username, client);
            try {
                dbxSpaceUsage.put(username, String.format("%.2f / %.2f GB",
                        (client.users().getSpaceUsage().getUsed() / (double)(1024*1024*1024)),
                        (client.users().getSpaceUsage().getAllocation().getIndividualValue().getAllocated() / (double)(1024*1024*1024))));
            } catch (DbxException e) {
                e.printStackTrace();
            }
            LOGGER.info(username + " client added.");
            return true;
        } else {
            LOGGER.info(username + " dropbox access token not found.");
            return false;
        }
    }

    @Override
    public void upload(String username, String dirPath, MultipartFile file) throws DbxException, IOException {
        upload(username, dirPath + "/" + file.getOriginalFilename(), file.getInputStream(), file.getSize());
    }

    @Override
    public void upload(String username, String fullPath, InputStream inputStream, long size) throws DbxException {
        LOGGER.info("upload() username={} fullPath={} size={}", username, fullPath, size);
        if (size < CHUNK_SIZE) {
            uploadSmall(username, fullPath, inputStream);
        } else {
            uploadBig(username, fullPath, inputStream, size);
        }
    }

    private void uploadSmall(String username, String path, InputStream inputStream) throws DbxException {
        LOGGER.info("uploadSmall() username={} dirPath={}", username, path);
        DbxClientV2 client = dbxClients.get(username);
        try {
            long startTime = System.currentTimeMillis();
            Metadata uploadedFile = client.files().upload(path).uploadAndFinish(inputStream);
            long endTime = System.currentTimeMillis();
            LOGGER.info("uploadSmall() finished in {}s username={} dirPath={}\n\tfile={}",
                    ((endTime-startTime)/1000), username, path, uploadedFile.toString());
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void uploadBig(String username, String path, InputStream inputStream, long size) throws DbxException {
        LOGGER.info("uploadBig() username={} dirPath={} size={}", username, path, size);
        DbxClientV2 client = dbxClients.get(username);
        try {
            long startTime = System.currentTimeMillis();

            long progress = 0;
            CommitInfo commitInfo = new CommitInfo(path);

            UploadSessionStartUploader uploader = client.files().uploadSessionStart();
            UploadSessionStartResult startResult = uploader.uploadAndFinish(inputStream, CHUNK_SIZE);
            progress += CHUNK_SIZE;

            UploadSessionAppendUploader appender;
            while (progress  + CHUNK_SIZE < size) {
                appender = client.files().uploadSessionAppend(startResult.getSessionId(), progress);
                appender.uploadAndFinish(inputStream, CHUNK_SIZE);
                progress += CHUNK_SIZE;
            }
            progress = progress > size ? size : progress;

            UploadSessionCursor cursor = new UploadSessionCursor(startResult.getSessionId(), progress);
            UploadSessionFinishUploader finisher = client.files().uploadSessionFinish(cursor, commitInfo);
            Metadata uploadedFile = finisher.uploadAndFinish(inputStream);
            long endTime = System.currentTimeMillis();
            LOGGER.info("uploadBig() finished in {}s username={} dirPath={} size={}\n\tfile={}",
                    ((endTime-startTime)/1000), username, path, size, uploadedFile.toString());
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void download(String username, String path, OutputStream outputStream) throws DbxException {
        LOGGER.info("download() username={} dirPath={}", username, path);
        DbxClientV2 client = dbxClients.get(username);
        try {
            DbxDownloader downloader = client.files().download(path);
            downloader.download(outputStream);
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public InputStream getInputStream(String username, String path) throws InvalidAccessTokenException {
        LOGGER.info("getInputStream() username={} dirPath={}", username, path);
        InputStream result = null;
        DbxClientV2 client = dbxClients.get(username);
        try {
            DbxDownloader downloader = client.files().download(path);
            result = downloader.getInputStream();
        } catch (InvalidAccessTokenException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

    @Override
    public void createFolder(String username, String path) throws DbxException {
        LOGGER.info("createFolder() username={} path={}", username, path);
        DbxClientV2 client = dbxClients.get(username);
        try {
            FolderMetadata createdFolder = client.files().createFolder(path);
            LOGGER.info(username + " created folder: " + createdFolder + ".");
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void delete(String username, String path) throws DbxException {
        LOGGER.info("delete() username={} path={}", username, path);
        DbxClientV2 client = dbxClients.get(username);
        try {
            Metadata deletedFile = client.files().delete(path);
            LOGGER.info("delete() success user={} file={}", username, deletedFile);
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public void restore(String username, String path) throws DbxException {
        LOGGER.info("restore() username={} dirPath={}", username, path);
        DbxClientV2 client = dbxClients.get(username);
        try {
            String rev = client.files().listRevisions(path).getEntries().get(0).getRev();
            FileMetadata restoredFile = client.files().restore(path, rev);
            LOGGER.info(username + " restored file: " + restoredFile);
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public String getStorageInfo(String username) throws DbxException {
        return dbxSpaceUsage.get(username);
    }

    @Override
    public void updateStorageInfo(String username) throws DbxException {
        LOGGER.info("updateStorageInfo() username=", username);
        DbxClientV2 client = dbxClients.get(username);
        try {
            dbxSpaceUsage.put(username, String.format("%.2f / %.2f GB",
                    (client.users().getSpaceUsage().getUsed() / (double) (1024 * 1024 * 1024)),
                    (client.users().getSpaceUsage().getAllocation().getIndividualValue().getAllocated() / (double) (1024 * 1024 * 1024))));
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    @Override
    public String getAccountInfo(String username) {
        LOGGER.info("getAccountInfo() username={}", username);
        DbxClientV2 client = dbxClients.get(username);
        String result = "";
        try {
            FullAccount acc = client.users().getCurrentAccount();
            result = acc.getEmail();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return result;
    }
}
