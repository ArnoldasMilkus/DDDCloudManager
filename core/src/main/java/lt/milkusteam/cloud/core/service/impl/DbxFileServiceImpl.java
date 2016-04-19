package lt.milkusteam.cloud.core.service.impl;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxDownloadStyleBuilder;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadSessionStartUploader;
import lt.milkusteam.cloud.core.dao.DbxTokenDao;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.service.DbxFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by gediminas on 4/17/16.
 */
@Service
public class DbxFileServiceImpl implements DbxFileService {

    HashMap<String, DbxClientV2> clients = new HashMap<>();

    @Autowired
    DbxTokenDao dbxTokenDao;

    @Override
    public List<Metadata> getFiles(String username, String path) {
        List<Metadata> result = new ArrayList<>();
        try {
            ListFolderResult folderResult = clients.get(username).files().listFolder(path);
            result = folderResult.getEntries();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Metadata getFileData(String username, String path) {
        Metadata result = null;
        DbxClientV2 client = clients.get(username);
        try {
            result = client.files().getMetadata(path);
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean containsClient(String username) {
        return clients.containsKey(username);
    }

    @Override
    public void removeClient(String username) {
        clients.remove(username);
    }

    @Override
    public void addClient(String username) {
        DbxRequestConfig config = new DbxRequestConfig("JustLearning", Locale.getDefault().toString());
        DbxToken accessToken = dbxTokenDao.findByUsername(username);
        DbxClientV2 client = new DbxClientV2(config, accessToken.getToken());
        clients.put(username, client);
    }

    @Override
    public void upload(String username, String path, InputStream fileStream) {
        DbxClientV2 client = clients.get(username);
        try {
            Metadata uploadedFile = client.files().upload(path).uploadAndFinish(fileStream);
            System.out.println("Uploaded file: " + uploadedFile);
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
