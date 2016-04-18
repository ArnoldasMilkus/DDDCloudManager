package lt.milkusteam.cloud.core.service;


import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;

import java.io.InputStream;
import java.util.List;

/**
 * Created by gediminas on 4/17/16.
 */
public interface DbxFileService {
    List<Metadata> getFiles(String username, String path);
    boolean containsUser(String username);
    void addClient(String username);
    void upload(String username, String path, InputStream fileStream);
}
