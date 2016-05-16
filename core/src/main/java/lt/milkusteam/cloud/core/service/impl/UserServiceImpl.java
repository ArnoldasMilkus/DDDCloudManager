package lt.milkusteam.cloud.core.service.impl;

import lt.milkusteam.cloud.core.dao.UserDao;
import lt.milkusteam.cloud.core.dao.VerificationTokenDao;
import lt.milkusteam.cloud.core.dao.repository.UserRepository;
import lt.milkusteam.cloud.core.model.*;
import lt.milkusteam.cloud.core.service.UserService;
import lt.milkusteam.cloud.core.validation.EmailExistsException;
import lt.milkusteam.cloud.core.validation.UsernameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by gediminas on 3/30/16.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserRepository repository;

    @Autowired
    private VerificationTokenDao verificationTokenDao;
    @Override
    public List<User> findAll() {
        return userDao.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
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
        return userDao.save(user);
    }

    private boolean emailExist(String email) {
        User user = userDao.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    private boolean usernameExist(String username) {
        User user = userDao.findByUsername(username);
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    public VerificationToken getVerificationToken(String VerificationToken) {
        return verificationTokenDao.findByToken(VerificationToken);
    }

    //use for registration via email for updating user's enable atribute
    @Override
    public void saveRegisteredUser(String username) {
        User user =userDao.findByUsername(username);
        user.setEnabled(true);
        repository.save(user);
    }

    @Override
    public void createVerificationToken(String username, String token) {
        VerificationToken myToken = new VerificationToken(token, username);
        verificationTokenDao.save(myToken);
    }

    private Collection<? extends GrantedAuthority>getAuthorities(Integer role){
        List<GrantedAuthority> authList = getGrantedAuthorities(getRoles(role));
        return authList;
    }

    private List<String> getRoles(Integer role) {
        List<String> roles = new ArrayList<String>();
        if (role.intValue() == 1) {
            roles.add("ROLE_USER");
            roles.add("ROLE_ADMIN");
        } else if (role.intValue() == 2) {
            roles.add("ROLE_USER");
        }
        return roles;
    }

    private static List<GrantedAuthority> getGrantedAuthorities (List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
