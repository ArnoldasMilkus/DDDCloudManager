package lt.milkusteam.cloud.core.service.impl;

import com.dropbox.core.*;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.users.SpaceUsage;
import lt.milkusteam.cloud.core.dao.DbxTokenDao;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.model.Pair;
import lt.milkusteam.cloud.core.service.DbxFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by gediminas on 4/17/16.
 */
@Service
public class DbxFileServiceImpl implements DbxFileService {

    private Map<String, DbxClientV2> dbxClients = new HashMap<>();
    private Map<String, String> dbxSpaceUsage = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(DbxFileServiceImpl.class);

    @Autowired
    private DbxTokenDao dbxTokenDao;


    @Override
    public Pair<List<FolderMetadata>, List<FileMetadata>> getMetadataPair(String username, String path) throws InvalidAccessTokenException {
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
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

    @Override
    public List<DeletedMetadata> getAllDeletedMetadata(String username) throws InvalidAccessTokenException {
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
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Metadata getMetadata(String username, String path) throws InvalidAccessTokenException {
        Metadata result = null;
        DbxClientV2 client = dbxClients.get(username);
        try {
            result = client.files().getMetadata(path, true);
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean containsClient(String username) {
        return dbxClients.containsKey(username);
    }

    @Override
    public void removeClient(String username) {
        dbxClients.remove(username);
        dbxSpaceUsage.remove(username);
        LOGGER.info(username + " client removed.");
    }

    @Override
    public boolean addClient(String username) {
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
    public void uploadSmall(String username, String path, InputStream inputStream) throws InvalidAccessTokenException {
        DbxClientV2 client = dbxClients.get(username);
        try {
            long starttime = System.currentTimeMillis();
            Metadata uploadedFile = client.files().upload(path).uploadAndFinish(inputStream);
            long endtime = System.currentTimeMillis();
            LOGGER.info(username + " uploaded small file in " + ((endtime-starttime)/1000) + "s: " + uploadedFile + ".");
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void uploadBig(String username, String path, InputStream inputStream, long size) throws InvalidAccessTokenException {
        DbxClientV2 client = dbxClients.get(username);
        try {
            long starttime = System.currentTimeMillis();

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
            long endtime = System.currentTimeMillis();
            LOGGER.info(username + " uploaded big file in " + ((endtime - starttime) / 1000) + "s: " + uploadedFile + ".");
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void download(String username, String path, OutputStream outputStream) throws InvalidAccessTokenException {
        DbxClientV2 client = dbxClients.get(username);
        try {
            DbxDownloader downloader = client.files().download(path);
            downloader.download(outputStream);
            LOGGER.info(username + " downloaded " + path + ".");
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public InputStream getInputStream(String username, String path) throws InvalidAccessTokenException {
        InputStream result = null;
        DbxClientV2 client = dbxClients.get(username);
        try {
            DbxDownloader downloader = client.files().download(path);
            result = downloader.getInputStream();
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

    @Override
    public void createFolder(String username, String path) throws InvalidAccessTokenException {
        DbxClientV2 client = dbxClients.get(username);
        try {
            client.files().createFolder(path);
            FolderMetadata createdFolder = client.files().createFolder(path);
            LOGGER.info(username + " created folder: " + createdFolder + ".");
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void delete(String username, String path) throws InvalidAccessTokenException {
        DbxClientV2 client = dbxClients.get(username);
        try {
            Metadata deletedFile = client.files().delete(path);
            LOGGER.info(username + " deleted file: " + deletedFile.getName());
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void restore(String username, String path) throws InvalidAccessTokenException {
        DbxClientV2 client = dbxClients.get(username);
        try {
            String rev = client.files().listRevisions(path).getEntries().get(0).getRev();
            FileMetadata restoredFile = client.files().restore(path, rev);
            LOGGER.info(username + " restored file: " + restoredFile);
        } catch (InvalidAccessTokenException e) {
            LOGGER.error("Invalid " + username + " dropbox access token.");
            throw e;
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public String getStorageInfo(String username) throws InvalidAccessTokenException {
        return dbxSpaceUsage.get(username);
    }
}
