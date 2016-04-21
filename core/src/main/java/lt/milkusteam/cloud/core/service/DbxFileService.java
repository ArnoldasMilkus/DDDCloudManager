package lt.milkusteam.cloud.core.service;


import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.FolderMetadata;
import com.dropbox.core.v2.files.Metadata;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by gediminas on 4/17/16.
 */
public interface DbxFileService {

    List<FileMetadata> getFilesMetadata(String username, String path);

    List<FolderMetadata> getFoldersMetadata(String username, String path);

    Metadata getMetadata(String username, String path);

    boolean containsClient(String username);

    void removeClient(String username);

    void addClient(String username);

    void upload(String username, String path, InputStream inputStream);

    void download(String username, String path, OutputStream outputStream);

    void createFolder(String username, String path);

    void delete(String username, String path);
}
