package lt.milkusteam.cloud.core.dao;

import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.model.GDriveToken;

/**
 * Created by Vilintas on 2016-05-13.
 */
public interface GDriveTokenDAO {

    GDriveToken findByUsername(String username);

    void save(GDriveToken token);

    void delete(String username);
}
