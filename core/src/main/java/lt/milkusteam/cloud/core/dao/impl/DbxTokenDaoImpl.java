package lt.milkusteam.cloud.core.dao.impl;

import lt.milkusteam.cloud.core.dao.DbxTokenDao;
import lt.milkusteam.cloud.core.dao.repository.DbxTokenRepository;
import lt.milkusteam.cloud.core.model.DbxToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by gediminas on 4/17/16.
 */
@Repository
public class DbxTokenDaoImpl implements DbxTokenDao {

    @Autowired
    private DbxTokenRepository dbxTokenRepository;

    @Override
    public DbxToken findByUsername(String username) {
        return dbxTokenRepository.findByUsername(username);
    }

    @Override
    public void save(DbxToken token) {
        dbxTokenRepository.save(token);
    }

    @Override
    public void delete(String username) {
        dbxTokenRepository.delete(username);
    }
}
