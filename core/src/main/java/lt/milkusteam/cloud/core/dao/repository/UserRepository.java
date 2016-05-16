package lt.milkusteam.cloud.core.dao.repository;

import lt.milkusteam.cloud.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by gediminas on 3/31/16.
 */
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    User findByUsername(String username);
}
