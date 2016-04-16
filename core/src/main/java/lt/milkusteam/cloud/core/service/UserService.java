package lt.milkusteam.cloud.core.service;

import lt.milkusteam.cloud.core.model.User;

import java.util.List;

/**
 * Created by gediminas on 3/30/16.
 */
public interface UserService {

    List<User> findAll();
    User findByUsername(String username);
    User findByEmail(String email);
}
