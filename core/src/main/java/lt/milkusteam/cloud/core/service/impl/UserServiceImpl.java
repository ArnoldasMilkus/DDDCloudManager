package lt.milkusteam.cloud.core.service.impl;

import lt.milkusteam.cloud.core.dao.UserDao;
import lt.milkusteam.cloud.core.dao.repository.UserRepository;
import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.model.UserDTO;
import lt.milkusteam.cloud.core.model.UserRole;
import lt.milkusteam.cloud.core.service.UserService;
import lt.milkusteam.cloud.core.validation.EmailExistsException;
import lt.milkusteam.cloud.core.validation.UsernameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by gediminas on 3/30/16.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    private UserRepository repository;
    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Transactional
    @Override
    public User registerNewUserAccount(UserDTO accountDto) throws EmailExistsException, UsernameExistsException {
        if (emailExist(accountDto.getEmail())) {
            throw new EmailExistsException("There is an account with that email address:"
                    + accountDto.getEmail());
        }
        if (usernameExist(accountDto.getUsername())) {
            throw new UsernameExistsException("There is an account with that username:"
                    + accountDto.getUsername());
        }
        User user = new User();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setUsername(accountDto.getUsername());
        user.setPassword(encoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        /*Set<UserRole> userRole = new HashSet<>();
        userRole.add(new UserRole(user,"ROLE_USER"));
        user.setUserRole(userRole);/*/
        user.setEnabled(true);
        //user.setRole(new Role(Integer.valueOf(1), user));
        return repository.save(user);
    }
    private boolean emailExist(String email) {
        User user = repository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }
    private boolean usernameExist(String username) {
        User user = repository.findByUsername(username);
        if (user != null) {
            return true;
        }
        return false;
    }

}
