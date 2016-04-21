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
import java.util.logging.Logger;

/**
 * Created by gediminas on 4/18/16.
 */
@Service
public class DbxAuthServiceImpl implements DbxAuthService {

    private final static Logger LOG = Logger.getLogger("DbxAuthServiceImpl");

    private final static String IDENTIFIER = "DDD Cloud Manager";

    private final static String KEY = "gcx6333853tq53z";

    private final static String SECRET = "a09ge1pm1sru3ie";

    private final static String REDIRECT_URI = "http://localhost:8080/dbx/auth-finish";

    private final static DbxAppInfo APP_INFO = new DbxAppInfo(KEY, SECRET);

    @Autowired
    private DbxTokenDao dbxTokenDao;

    private HashMap<String, DbxWebAuth> activeWebAuths = new HashMap<>();

    @Override
    public String startAuth(String username, DbxSessionStore store) {
        DbxRequestConfig config = new DbxRequestConfig(IDENTIFIER, Locale.getDefault().toString());
        DbxWebAuth auth = new DbxWebAuth(config, APP_INFO, REDIRECT_URI, store);
        activeWebAuths.put(username, auth);
        return auth.start();
    }

    @Override
    public void finishAuth(String username, String state, String code) {

        DbxWebAuth webAuth = activeWebAuths.get(username);
        if (webAuth != null) {
            Map<String, String[]> params = new HashMap<>();
            params.put("state", new String[]{state});
            params.put("code", new String[]{code});

            DbxAuthFinish authResult = null;
            try {
                authResult = webAuth.finish(params);
            } catch (Exception e) {
                // ARBA http://www.mkyong.com/logging/log4j-hello-world-example/
                LOG.severe(e.getMessage());
            }

            if (authResult != null) {
                DbxToken token = new DbxToken(username, authResult.getAccessToken());
                dbxTokenDao.save(token);
            }
            activeWebAuths.remove(username);
        }
    }

    @Override
    public void undoAuth(String username) {
        dbxTokenDao.delete(username);
    }

    @Override
    public boolean isLinked(String username) {
        return dbxTokenDao.findByUsername(username) != null;
    }
}
