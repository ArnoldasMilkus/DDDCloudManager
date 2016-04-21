package lt.milkusteam.cloud.core.service;

import com.google.api.services.drive.model.File;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Vilintas on 2016-04-20.
 */
public interface GDriveFilesService {

    List<File> findAllInDirectory(String directoryId, String userName);

    String addToPath(File folder);

    String removeFromPathLast();

    String getIfChild(String childId, String userName);

    File uploadFile(InputStream inStream, String parentId, String fileName, String userName, boolean useDirectUpload);
}
