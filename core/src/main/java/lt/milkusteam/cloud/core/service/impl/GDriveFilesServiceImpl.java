package lt.milkusteam.cloud.core.service.impl;

import com.google.api.services.drive.model.File;
import lt.milkusteam.cloud.core.GDriveAPI.GDrive;
import lt.milkusteam.cloud.core.GDriveAPI.GDriveDownloader;
import lt.milkusteam.cloud.core.GDriveAPI.GDriveUploader;
import lt.milkusteam.cloud.core.dao.GDriveTokenDAO;
import lt.milkusteam.cloud.core.model.GDriveToken;
import lt.milkusteam.cloud.core.service.GDriveFilesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vilintas Strielčiūnas on 2016-04-20.
 */
@Service
public class GDriveFilesServiceImpl implements GDriveFilesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDriveFilesServiceImpl.class);

    @Autowired
    private GDriveTokenDAO GDriveTokenDAO;

    private HashMap<String, List<GDrive>> driveMap = new HashMap<>();

    @Override
    public List<File> findAllInDirectory(String directoryId, String userName, boolean isTrashed, int ind) {
        return getDriveService(userName, ind).getListByParentId(directoryId, isTrashed);
    }

    @Override
    public String getIfChild(String childId, String userName, int ind) {
        return getDriveService(userName, ind).getParentId(childId);
    }

    @Override
    public File uploadFile(InputStream inStream, String parentId, String fileName, String userName, boolean useDirectUpload, int ind) {
        GDrive drive = getDriveService(userName, ind);
        GDriveUploader uploader = new GDriveUploader();
        File metaData = new File();
        List<String> parents = new ArrayList<>();
        parents.add(parentId);
        metaData.setParents(parents);
        metaData.setName(fileName);
        return uploader.simpleUploadStream(drive.getDrive(), metaData, inStream, useDirectUpload);
    }

    private GDrive getDriveService(String userName, int ind) {
        if (driveMap == null) {
            driveMap = new HashMap<>();
        }
        if (driveMap.get(userName) == null || driveMap.get(userName).isEmpty()) {
            addClient(userName);
        }
        return driveMap.get(userName).get(ind);
    }

    @Override
    public boolean containsClient(String username, int ind) {
        return driveMap.containsKey(username) && driveMap.get(username) != null && driveMap.get(username).size() >= ind+1;
    }

    @Override
    public void removeClient(String username, int ind) {
        List<GDrive> list = driveMap.get(username);
        if (ind > list.size()-1) {
            LOGGER.error("Wrong delete index: list size " + list.size() + ", index " + ind);
            return;
        }
        list.remove(ind);
        driveMap.put(username, list);
        LOGGER.info(username + " client removed.");
    }

    @Override
    public int addClient(String username) {
        GDriveToken token = GDriveTokenDAO.findByUsername(username);
        GDrive client = new GDrive(token.getUsername(), "0", token.getToken());
        List<GDrive> list = driveMap.get(username);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(client);
        LOGGER.info(username + " client added.");
        driveMap.put(username, list);
        return list.size()-1;
    }

    @Override
    public void revokeToken(String username, int ind) {
        GDrive drive = getDriveService(username, ind);
        String token = GDriveTokenDAO.findByUsername(username).getToken();
        GDriveTokenDAO.delete(username);
        removeClient(username, ind);
        drive.revokeToken(token);
    }

    @Override
    public void downloadToClient(HttpServletResponse response, String fileId, String username, int ind) {
        new GDriveDownloader().downloadToClient(getDriveService(username, ind).getDrive(), response, fileId);
    }

    @Override
    public void fixTrashed(String username, int ind, boolean trashed, String fileId) {
        GDrive drive = getDriveService(username, ind);
        drive.setTrashed(fileId, trashed);
    }

    @Override
    public void newFolder(String username, int ind, String folderName, String parentId) {
        GDrive drive = getDriveService(username, ind);
        drive.createFolder(folderName, parentId);
    }

    @Override
    public InputStream returnStream(String username, int ind, String fileId) {
        GDrive drive = getDriveService(username, ind);
        try {
            return drive.getDrive().files().get(fileId).executeMedia().getContent();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public String getName(String username, int ind, String fileId) {
        GDrive drive = getDriveService(username, ind);
        try {
            return drive.getDrive().files().get(fileId).setFields("name").execute().getName();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    @Override
    public long getSize(String username, int ind, String fileId) {
        GDrive drive = getDriveService(username, ind);
        try {
            return drive.getDrive().files().get(fileId).setFields("size").execute().getSize();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return 0;
    }
}
