package lt.milkusteam.cloud.core.service;


import com.dropbox.core.v2.files.Metadata;

import java.io.InputStream;
import java.util.List;

/**
 * Created by gediminas on 4/17/16.
 */
public interface DbxFileService {
    List<Metadata> getFiles(String username, String path);
    Metadata getFile(String username, String path);
    boolean containsClient(String username);
    void removeClient(String username);
    void addClient(String username);
    void upload(String username, String path, InputStream fileStream);
    void createFolder(String username, String path);
}
