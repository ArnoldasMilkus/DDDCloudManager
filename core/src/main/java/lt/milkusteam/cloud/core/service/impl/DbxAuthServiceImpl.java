package lt.milkusteam.cloud.core.service.impl;

import com.dropbox.core.*;
import lt.milkusteam.cloud.core.dao.DbxTokenDao;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.service.DbxAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by gediminas on 4/18/16.
 */
@Service
public class DbxAuthServiceImpl implements DbxAuthService {
    private static final String IDENTIFIER = "DDD Cloud Manager";
    private static final String KEY = "gcx6333853tq53z";
    private static final String SECRET = "a09ge1pm1sru3ie";
    private static final String REDIRECT_URI = "http://localhost:8080/dbx/auth-finish";
    private static final DbxAppInfo APP_INFO = new DbxAppInfo(KEY, SECRET);

    @Autowired
    DbxTokenDao dbxTokenDao;

    HashMap<String, DbxWebAuth> activeWebAuths = new HashMap<>();

    @Override
    public String startAuth(String username, DbxSessionStore store) {
        DbxRequestConfig config = new DbxRequestConfig(IDENTIFIER, Locale.getDefault().toString());
        DbxWebAuth auth = new DbxWebAuth(config, APP_INFO, REDIRECT_URI, store);
        activeWebAuths.put(username, auth);
        String authUrl = auth.start();
        return authUrl;
    }

    @Override
    public String finishAuth(String username, String state, String code) {
        String result = "files?path=";

        DbxWebAuth webAuth = activeWebAuths.get(username);
        if (webAuth != null) {
            Map<String, String[]> params = new HashMap<>();
            params.put("state", new String[]{state});
            params.put("code", new String[]{code});

            DbxAuthFinish authResult = null;
            try {
                authResult = webAuth.finish(params);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (authResult != null) {
                DbxToken token = new DbxToken(username, authResult.getAccessToken());
                dbxTokenDao.save(token);
            }
            activeWebAuths.remove(username);
        }
        return result;
    }


    @Override
    public String undoAuth(String username) {
        dbxTokenDao.delete(username);
        return "files?path=";
    }

    @Override
    public boolean isLinked(String username) {
        return dbxTokenDao.findByUsername(username) != null;
    }
}
