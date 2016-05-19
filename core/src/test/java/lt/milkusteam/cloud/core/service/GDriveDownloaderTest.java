package lt.milkusteam.cloud.core.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import lt.milkusteam.cloud.core.GDriveAPI.GDrive;
import lt.milkusteam.cloud.core.GDriveAPI.GDriveDownloader;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vilintas on 2016-05-18.
 */
public class GDriveDownloaderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDriveDownloaderTest.class);
    private GDrive gDrive;


    private static final String USER_ID = "vilstr32";
    private static final String DRIVE_ID = "001";
    private static final String REFRESH_TOKEN = "1/e5SMK2LXZn2Y3Pt1h4nEo49KlVaR58NGXZWetVbnmqE";
    private static final String TEST_FILE_ID = "0B2iI6jzmh9j_Zko0Qk55Tl9BVkE";

    private static final String MIME_FILE = "application/octet-stream";

    @Autowired
    HttpServletResponse servletResponse;

    @Before
    public void setUp() {
        gDrive = new GDrive();
        GoogleTokenResponse resp = new GoogleTokenResponse().setRefreshToken(REFRESH_TOKEN);
        gDrive.initGDrive(USER_ID, DRIVE_ID, resp);
    }

    @Test
    public void testDownloadToClient() {
        servletResponse = new MockHttpServletResponse();
            new GDriveDownloader().downloadToClient(gDrive.getDrive(), servletResponse, TEST_FILE_ID);
        assertEquals(MIME_FILE, servletResponse.getContentType());
    }
}
