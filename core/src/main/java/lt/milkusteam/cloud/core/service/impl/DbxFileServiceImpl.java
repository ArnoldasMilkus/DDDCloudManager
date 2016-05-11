package lt.milkusteam.cloud.core.service.impl;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxUploader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import lt.milkusteam.cloud.core.dao.DbxTokenDao;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.service.DbxFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by gediminas on 4/17/16.
 */
@Service
public class DbxFileServiceImpl implements DbxFileService {

    public final static int CHUNK_SIZE = 104857600;

    private HashMap<String, DbxClientV2> clients = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(DbxFileServiceImpl.class);

    @Autowired
    private DbxTokenDao dbxTokenDao;

    @Override
    public List<FileMetadata> getFilesMetadata(String username, String path) {
        List<FileMetadata> result = new ArrayList<>();
        try {
            ListFolderResult folderResult = clients.get(username).files().listFolder(path);
            List<Metadata> temp = folderResult.getEntries();
            for (Metadata data : temp) {
                if (data instanceof FileMetadata) {
                    result.add((FileMetadata) data);
                    System.out.println(((FileMetadata) data).getMediaInfo());
                }
            }
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

    @Override
    public List<FolderMetadata> getFoldersMetadata(String username, String path) {
        List<FolderMetadata> result = new ArrayList<>();
        try {
            ListFolderResult folderResult = clients.get(username).files().listFolder(path);
            List<Metadata> temp = folderResult.getEntries();
            for (Metadata data : temp) {
                if (data instanceof FolderMetadata) {
                    result.add((FolderMetadata) data);
                } else {
                    break;
                }
            }
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }

    @Override
    public Metadata getMetadata(String username, String path) {
        Metadata result = null;
        DbxClientV2 client = clients.get(username);
        try {
            result = client.files().getMetadata(path);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean containsClient(String username) {
        return clients.containsKey(username);
    }

    @Override
    public void removeClient(String username) {
        clients.remove(username);
        LOGGER.info(username + " client removed.");
    }

    @Override
    public void addClient(String username) {
        DbxRequestConfig config = new DbxRequestConfig("DDD Cloud Manager", Locale.getDefault().toString());
        DbxToken accessToken = dbxTokenDao.findByUsername(username);
        DbxClientV2 client = new DbxClientV2(config, accessToken.getToken());
        LOGGER.info(username + " client added.");
        clients.put(username, client);
    }

    @Override
    public void uploadSmall(String username, String path, InputStream inputStream) {
        DbxClientV2 client = clients.get(username);
        try {
            long starttime = System.currentTimeMillis();
            Metadata uploadedFile = client.files().upload(path).uploadAndFinish(inputStream);
            long endtime = System.currentTimeMillis();
            LOGGER.info(username + " uploaded small file in " + ((endtime-starttime)/1000) + "s: " + uploadedFile + ".");
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void uploadBig(String username, String path, InputStream inputStream, long size) {
        DbxClientV2 client = clients.get(username);
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
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void download(String username, String path, OutputStream outputStream) {
        DbxClientV2 client = clients.get(username);
        try {
            DbxDownloader downloader = client.files().download(path);
            downloader.download(outputStream);
            LOGGER.info(username + " downloaded " + path + ".");
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void createFolder(String username, String path) {
        DbxClientV2 client = clients.get(username);
        try {
            client.files().createFolder(path);
            FolderMetadata createdFolder = client.files().createFolder(path);
            LOGGER.info(username + " created folder: " + createdFolder + ".");
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void delete(String username, String path) {
        DbxClientV2 client = clients.get(username);
        try {
            Metadata deletedFile = client.files().delete(path);
            LOGGER.info(username + " deleted file: " + deletedFile.getName());
        } catch (DbxException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
