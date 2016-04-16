package lt.milkusteam.cloud.core.service;

import lt.milkusteam.cloud.core.model.UserRole;
import lt.milkusteam.cloud.core.service.impl.SimpleUserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by gediminas on 4/14/16.
 */
public class SimpleUserDetailsServiceImplTest {

    SimpleUserDetailsServiceImpl userDetailsService;

    @Before
    public void setup() {
        userDetailsService = new SimpleUserDetailsServiceImpl();
    }

    @Test
    public void testBuildAuthority() {
        lt.milkusteam.cloud.core.model.User myUser =
                new lt.milkusteam.cloud.core.model.User("username", "email@email.com", "password1", true);
        Set<UserRole> myUserAuthorities = new HashSet<>();
        UserRole myUserRole = new UserRole(myUser, "ROLE_TEST");
        myUserRole.setUserRoleId(1);
        myUserAuthorities.add(myUserRole);

        User springUser = userDetailsService.buildSpringUser(myUser, userDetailsService.buildUserAuthority(myUserAuthorities));
        assertEquals(springUser.getUsername(), myUser.getUsername());
        assertEquals(springUser.getPassword(), myUser.getPassword());

        Collection<GrantedAuthority> springUserAuthorities = springUser.getAuthorities();
        assertEquals(springUserAuthorities.size(), myUserAuthorities.size());
    }
}
