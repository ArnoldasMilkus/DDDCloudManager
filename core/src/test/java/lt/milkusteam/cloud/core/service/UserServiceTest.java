package lt.milkusteam.cloud.core.service;

import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.service.impl.UserServiceImpl;
import lt.milkusteam.cloud.core.dao.UserDao;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * Created by gediminas on 3/30/16.
 */
public class UserServiceTest {

    private UserService userService;

    @Before
    public void setUp() {
        userService = new UserServiceImpl();
        UserDao userDao = mock(UserDao.class);
        when(userDao.findAll()).thenReturn(getMockAllUsersList());
        setField(userService, "userDao", userDao);
    }

    @Test
    public void testFindAll() {
        List<User> allUsers = userService.findAll();

        assertEquals(2, allUsers.size());

        User john = allUsers.get(0);
        assertEquals("bravo@email.com", john.getEmail());

        User jane = allUsers.get(1);
        assertEquals("unknown@email.com", jane.getEmail());

    }

    public List<User> getMockAllUsersList() {
        List<User> result = new ArrayList<>();
        result.add(new User("JohnD1999", "bravo@email.com", "ThisIsPassword123"));
        result.add(new User("JaneD2000", "unknown@email.com", "password"));
        return result;
    }
}
