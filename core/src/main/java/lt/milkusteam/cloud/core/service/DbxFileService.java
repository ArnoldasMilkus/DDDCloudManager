package lt.milkusteam.cloud.core.service;


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

    Pair<List<FolderMetadata>, List<FileMetadata>> getMetadataPair(String username, String path);

    List<DeletedMetadata> getAllDeletedMetadata(String username);

    Metadata getMetadata(String username, String path);

    boolean containsClient(String username);

    void removeClient(String username);

    void addClient(String username);

    void uploadSmall(String username, String path, InputStream inputStream);

    void uploadBig(String username, String path, InputStream inputStream, long size);

    void download(String username, String path, OutputStream outputStream);

    void createFolder(String username, String path);

    void delete(String username, String path);

    void permDelete(String username, String path);

    void restore(String username, String path);

    FullAccount getAccountInfo(String username);
    SpaceUsage getStorageInfo(String username);
}
