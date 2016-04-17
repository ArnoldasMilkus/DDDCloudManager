package lt.milkusteam.cloud.core.service;

import lt.milkusteam.cloud.core.model.DbxToken;

/**
 * Created by gediminas on 4/17/16.
 */
public interface DbxTokenService {
    DbxToken findByUsername(String username);
    void save(DbxToken token);
    void delete(String username);
}
