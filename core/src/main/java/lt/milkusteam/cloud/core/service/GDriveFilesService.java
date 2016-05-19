package lt.milkusteam.cloud.core.service;

import com.google.api.services.drive.model.File;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-20.
 */
public interface GDriveFilesService {

    List<File> findAllInDirectory(String directoryId, String userName, boolean isTrashed, int ind);

    String getIfChild(String childId, String userName, int ind);

    File uploadFile(InputStream inStream, String parentId, String fileName, String userName, boolean useDirectUpload, int ind);

    boolean containsClient(String username, int ind);

    void removeClient(String username, int ind);

    int addClient(String username);

    void revokeToken(String username, int ind);

    void downloadToClient(HttpServletResponse response, String fileId, String username, int ind);

    void fixTrashed(String username, int ind, boolean trashed, String fileId);

    String newFolder(String username, int ind, String folderName, String parentId);

    InputStream returnStream(String username, int ind, String fileId);

    String getName(String username, int ind, String fileId);

    long getSize(String username, int ind, String fileId);

    String getEmail(String username, int ind);
}
