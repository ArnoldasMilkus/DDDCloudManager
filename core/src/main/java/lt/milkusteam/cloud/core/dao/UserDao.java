package lt.milkusteam.cloud.core.dao;

import lt.milkusteam.cloud.core.model.User;

import java.util.List;

/**
 * Created by gediminas on 3/31/16.
 */
public interface UserDao {
    List<User> findAll();
    User findByUsername(String username);
    User findByEmail(String email);
    User save(User user);
}
