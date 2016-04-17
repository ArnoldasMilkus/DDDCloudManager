package lt.milkusteam.cloud.core.service.impl;

import lt.milkusteam.cloud.core.dao.DbxTokenDao;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.service.DbxTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by gediminas on 4/17/16.
 */
@Service
public class DbxTokenServiceImpl implements DbxTokenService {

    @Autowired
    DbxTokenDao dbxTokenDao;

    @Override
    public DbxToken findByUsername(String username) {
        return dbxTokenDao.findByUsername(username);
    }

    @Override
    public void save(DbxToken token) {
        dbxTokenDao.save(token);
    }

    @Override
    public void delete(String username) {
        dbxTokenDao.delete(username);
    }

}
