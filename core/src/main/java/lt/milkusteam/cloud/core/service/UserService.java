package lt.milkusteam.cloud.core.service;

import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.model.UserDTO;
import lt.milkusteam.cloud.core.validation.EmailExistsException;

import java.util.List;

/**
 *
 */
public interface UserService {

    List<User> findAll();
    User findByUsername(String username);
    User findByEmail(String email);
    User registerNewUserAccount(UserDTO accountDto)
            throws EmailExistsException;
}
