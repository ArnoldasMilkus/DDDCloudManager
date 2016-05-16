package lt.milkusteam.cloud.core.dao.impl;

import lt.milkusteam.cloud.core.dao.GDriveTokenDAO;
import lt.milkusteam.cloud.core.dao.repository.GDriveTokenRepository;
import lt.milkusteam.cloud.core.model.GDriveToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Vilintas on 2016-05-13.
 */
@Repository
public class GDriveTokenDAOImpl implements GDriveTokenDAO {

    @Autowired
    private GDriveTokenRepository gDriveTokenRep;

    @Override
    public GDriveToken findByUsername(String username) {
        return gDriveTokenRep.findByUsername(username);
    }

    @Override
    public void save(GDriveToken token) {
        gDriveTokenRep.save(token);
    }

    @Override
    public void delete(String username) {
        gDriveTokenRep.delete(username);
    }
}
