package lt.milkusteam.cloud.core.service;

import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.model.UserDTO;
import lt.milkusteam.cloud.core.model.VerificationToken;
import lt.milkusteam.cloud.core.validation.EmailExistsException;
import lt.milkusteam.cloud.core.validation.UsernameExistsException;

import java.util.List;

/**
 *
 */
public interface UserService {

    List<User> findAll();

    User findByUsername(String username);

    User registerNewUserAccount(UserDTO accountDto)
            throws EmailExistsException, UsernameExistsException;

    void saveRegisteredUser(String username);

    void createVerificationToken(String username, String token);

    VerificationToken getVerificationToken(String VerificationToken);




}
