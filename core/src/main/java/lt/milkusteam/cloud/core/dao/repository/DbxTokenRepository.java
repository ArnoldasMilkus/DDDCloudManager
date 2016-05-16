package lt.milkusteam.cloud.core.dao.repository;

import lt.milkusteam.cloud.core.model.DbxToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by gediminas on 3/31/16.
 */
public interface DbxTokenRepository extends JpaRepository<DbxToken, String> {

    DbxToken findByUsername(String username);
}
