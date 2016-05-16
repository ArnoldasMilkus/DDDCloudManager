package lt.milkusteam.cloud.core.service;


import com.dropbox.core.InvalidAccessTokenException;
import com.dropbox.core.v2.files.DeletedMetadata;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;
import com.dropbox.core.v2.users.SpaceUsage;
import lt.milkusteam.cloud.core.model.Pair;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by gediminas on 4/17/16.
 */
public interface DbxFileService {
    int CHUNK_SIZE = 104857600;

    Pair<List<FolderMetadata>, List<FileMetadata>> getMetadataPair(String username, String path) throws InvalidAccessTokenException;

    List<DeletedMetadata> getAllDeletedMetadata(String username) throws InvalidAccessTokenException;

    Metadata getMetadata(String username, String path) throws InvalidAccessTokenException;

    boolean containsClient(String username);

    void removeClient(String username);

    boolean addClient(String username);

    void uploadSmall(String username, String path, InputStream inputStream) throws InvalidAccessTokenException;

    void uploadBig(String username, String path, InputStream inputStream, long size) throws InvalidAccessTokenException;

    void download(String username, String path, OutputStream outputStream) throws InvalidAccessTokenException;

    InputStream getInputStream(String username, String path) throws InvalidAccessTokenException;

    void createFolder(String username, String path) throws InvalidAccessTokenException;

    void delete(String username, String path) throws InvalidAccessTokenException;

    void restore(String username, String path) throws InvalidAccessTokenException;

    String getStorageInfo(String username) throws InvalidAccessTokenException;

    void updateStorageInfo(String username) throws InvalidAccessTokenException;

    String getAccountInfo(String username);
}
