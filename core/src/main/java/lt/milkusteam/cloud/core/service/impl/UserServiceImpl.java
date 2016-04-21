package lt.milkusteam.cloud.core.service.impl;

import lt.milkusteam.cloud.core.dao.UserDao;
import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by gediminas on 3/30/16.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

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
}
