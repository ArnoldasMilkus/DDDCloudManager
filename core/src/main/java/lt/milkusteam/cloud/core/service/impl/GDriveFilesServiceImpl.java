package lt.milkusteam.cloud.core.service.impl;

import com.google.api.services.drive.model.File;
import lt.milkusteam.cloud.core.GDriveAPI.GDrive;
import lt.milkusteam.cloud.core.GDriveAPI.GDriveUploader;
import lt.milkusteam.cloud.core.service.GDriveFilesService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vilintas on 2016-04-20.
 */
@Service
public class GDriveFilesServiceImpl implements GDriveFilesService {

    private HashMap<String, List<GDrive>> driveMap;

    @Override
    public List<File> findAllInDirectory(String directoryId, String userName) {
        if (directoryId == null || directoryId.isEmpty()) {
            directoryId = new String("root");
        }
        return getDriveService(userName).getListByParentId(directoryId);
    }

    @Override
    public String addToPath(File folder) {
        return null;
    }

    @Override
    public String removeFromPathLast() {
        return null;
    }

    @Override
    public String getIfChild(String childId, String userName) {
        return getDriveService(userName).getParentId(childId);
    }

    @Override
    public File uploadFile(InputStream inStream, String parentId, String fileName, String userName, boolean useDirectUpload) {
        GDrive drive = getDriveService(userName);
        GDriveUploader uploader = new GDriveUploader();
        File metaData = new File();
        List<String> parents = new ArrayList<>();
        parents.add(parentId);
        metaData.setParents(parents);
        metaData.setName(fileName);
        return uploader.simpleUploadStream(drive.getDRIVE_SERVICE(), metaData, inStream, useDirectUpload);
    }

    private GDrive getDriveService(String userName) {
        if (driveMap == null) {
            driveMap = new HashMap<>();
        }
        if (driveMap.get(userName) == null || driveMap.get(userName).isEmpty()) {
            List<GDrive> list = new ArrayList<>();
            GDrive drive = new GDrive(userName, "drive001");
            list.add(drive);
            driveMap.put(userName, list);
            return drive;
        }
        else {
            return driveMap.get(userName).get(0);
        }
    }

}
