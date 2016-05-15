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
    private GDriveTokenRepository GDriveTokenRep;

    @Override
    public GDriveToken findByUsername(String username) {
        return GDriveTokenRep.findByUsername(username);
    }

    @Override
    public void save(GDriveToken token) {
        GDriveTokenRep.save(token);
    }

    @Override
    public void delete(String username) {
        GDriveTokenRep.delete(username);
    }
}
