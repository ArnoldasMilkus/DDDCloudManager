package lt.milkusteam.cloud.core.dao.repository;

import lt.milkusteam.cloud.core.model.GDriveToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Vilintas on 2016-05-13.
 */
public interface GDriveTokenRepository extends JpaRepository<GDriveToken, String> {
    GDriveToken findByUsername(String username);
}
