package lt.milkusteam.cloud.core.service.impl;

import lt.milkusteam.cloud.core.dao.UserDao;
import lt.milkusteam.cloud.core.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by gediminas on 4/14/16.
 */
@Service
public class SimpleUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        lt.milkusteam.cloud.core.model.User user = userDao.findByUsername(username);
        List<GrantedAuthority> authorities = buildUserAuthority(user.getUserRole());
        return buildSpringUser(user, authorities);
    }

    public User buildSpringUser(lt.milkusteam.cloud.core.model.User user, List<GrantedAuthority> authorities) {
        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true, authorities);
    }

    public List<GrantedAuthority> buildUserAuthority(Set<UserRole> userRoles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority(userRole.getRole()));
        }
        return authorities;
    }

}
