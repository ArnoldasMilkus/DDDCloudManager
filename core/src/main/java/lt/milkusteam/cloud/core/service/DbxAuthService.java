package lt.milkusteam.cloud.core.service;

import com.dropbox.core.DbxSessionStore;

/**
 * Created by gediminas on 4/18/16.
 */
public interface DbxAuthService {
    String startAuth(String username, DbxSessionStore store);
    String finishAuth(String username, String state, String code);
    String undoAuth(String username);
    boolean isLinked(String username);
}
