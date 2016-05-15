package lt.milkusteam.cloud.core.service;

import com.google.api.services.drive.model.File;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-20.
 */
public interface GDriveFilesService {

    List<File> findAllInDirectory(String directoryId, String userName, boolean isTrashed);

    String getIfChild(String childId, String userName);

    File uploadFile(InputStream inStream, String parentId, String fileName, String userName, boolean useDirectUpload);

    boolean containsClient(String username, int ind);

    void removeClient(String username, int ind);

    int addClient(String username);

    void revokeToken(String username, int ind);

    void downloadToClient(HttpServletResponse response, String fileId, String username, int ind);

    void fixTrashed(String username, int ind, boolean trashed, String fileId);

    void newFolder(String username, int ind, String folderName, String parentId);
}
