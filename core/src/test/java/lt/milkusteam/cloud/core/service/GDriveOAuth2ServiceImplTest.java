package lt.milkusteam.cloud.core.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import lt.milkusteam.cloud.core.GDriveAPI.GDrive;
import lt.milkusteam.cloud.core.dao.GDriveTokenDAO;
import lt.milkusteam.cloud.core.dao.impl.GDriveTokenDAOImpl;
import lt.milkusteam.cloud.core.model.GDriveToken;
import lt.milkusteam.cloud.core.service.impl.GDriveOAuth2ServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * Created by gediminas on 3/30/16.
 */
public class GDriveOAuth2ServiceImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDriveOAuth2ServiceImplTest.class);

    private static final String USER_ID = "vilstr32";
    private static final String NON_USER_ID = "neegzistuoja";
    private static final String DRIVE_ID = "001";
    private static final String WRONG_CODE = "abc";
    private static final String REFRESH_TOKEN = "1/e5SMK2LXZn2Y3Pt1h4nEo49KlVaR58NGXZWetVbnmqE";
    private static final String TOKEN_ERROR = "invalid_grant";
    private static final String ACTIVATION_URL = "https://accounts.google.com/o/oauth2/auth?access_type=offline&client_id=851556385458-s35qkeog0h3ph9pn7sbot0klvks3hpr5.apps.googleusercontent.com&redirect_uri=http://localhost:8080/GDriveFiles/finishAuth&response_type=code&scope=https://www.googleapis.com/auth/drive&state=/profile";

    private GDriveOAuth2Service gDriveOAuth2Service;
    private GDrive drive;

    @Before
    public void setUp() {
        gDriveOAuth2Service = new GDriveOAuth2ServiceImpl();
        drive = new GDrive();
        GoogleTokenResponse resp = new GoogleTokenResponse().setRefreshToken(REFRESH_TOKEN);
        drive.initGDrive(USER_ID, DRIVE_ID, resp);
        GDriveTokenDAO gDriveTokenDAO = mock(GDriveTokenDAOImpl.class);
        when(gDriveTokenDAO.findByUsername(USER_ID)).thenReturn(new GDriveToken(USER_ID, REFRESH_TOKEN));
        when(gDriveTokenDAO.findByUsername(NON_USER_ID)).thenReturn(null);
        setField(gDriveOAuth2Service, "gDriveTokenDAO", gDriveTokenDAO);
    }

    @Test
    public void testGetIsLinked() {
        boolean isLinked = gDriveOAuth2Service.isLinked(USER_ID);
        assertEquals(true, isLinked);
        isLinked = gDriveOAuth2Service.isLinked(NON_USER_ID);
        assertEquals(false, isLinked);
    }

    @Test
    public void testGetActivationURL() {
        String url = gDriveOAuth2Service.getActivationURL();
        assertEquals(ACTIVATION_URL, url);

    }

    @Test
    public void testRequestRefreshToken() {
        String token = gDriveOAuth2Service.requestRefreshToken(USER_ID, WRONG_CODE);
        assertEquals(TOKEN_ERROR, token);
    }
}
