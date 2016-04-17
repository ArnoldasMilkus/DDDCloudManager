package lt.milkusteam.cloud.core.service.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.service.DbxFileService;
import lt.milkusteam.cloud.core.service.DbxTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    DbxTokenService dbxTokenService;

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
    public boolean contains(String username) {
        return clients.containsKey(username);
    }

    @Override
    public void addClient(String username) {
        DbxRequestConfig config = new DbxRequestConfig("JustLearning", Locale.getDefault().toString());
        DbxToken accessToken = dbxTokenService.findByUsername(username);
        DbxClientV2 client = new DbxClientV2(config, accessToken.getToken());
        clients.put(username, client);
    }
}
