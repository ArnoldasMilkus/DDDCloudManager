package lt.milkusteam.cloud.core.service.impl;

import com.dropbox.core.*;
import lt.milkusteam.cloud.core.dao.DbxTokenDao;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.service.DbxAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by gediminas on 4/18/16.
 */
@Service
public class DbxAuthServiceImpl implements DbxAuthService {

    private final static Logger LOGGER = LoggerFactory.getLogger(DbxAuthServiceImpl.class);

    @Value("${dbx.identifier}")
    private String identifier;

    @Value("${dbx.key}")
    private String key;

    @Value("${dbx.identifier}")
    private String secret;

    @Value("${dbx.redirect-uri}")
    private String redirect_uri;

    private final DbxAppInfo app_info = new DbxAppInfo(key, secret);

    @Autowired
    private DbxTokenDao dbxTokenDao;

    private HashMap<String, DbxWebAuth> activeWebAuths = new HashMap<>();

    @Override
    public String startAuth(String username, DbxSessionStore store) {
        DbxRequestConfig config = new DbxRequestConfig(identifier, Locale.getDefault().toString());
        DbxWebAuth auth = new DbxWebAuth(config, app_info, redirect_uri, store);
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
                LOGGER.error(e.getMessage());
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
}
