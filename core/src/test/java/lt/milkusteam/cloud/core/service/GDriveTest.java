package lt.milkusteam.cloud.core.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.drive.model.File;
import lt.milkusteam.cloud.core.GDriveAPI.GDrive;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vilintas on 2016-05-18.
 */
public class GDriveTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDriveTest.class);
    private GDrive gDrive;

    private static final String USER_ID = "test";
    private static final String DRIVE_ID = "vilstr32";
    private static final String REFRESH_TOKEN = "1/e5SMK2LXZn2Y3Pt1h4nEo49KlVaR58NGXZWetVbnmqE";
    private static final String TEST_ROOT_ID = "0B2iI6jzmh9j_dDQtTlBTWndKNEU";
    private static final String TEST_FILE_ID = "0B2iI6jzmh9j_Zko0Qk55Tl9BVkE";
    private static final String TEST_TRASHED_ID = "0B2iI6jzmh9j_M0dlWmlzN2JmZE0";
    private static final String TEST_FILE_NAME = "ForUnitTest.txt";
    private static final String TEST_TRASHED_NAME = "Trash.txt";
    private static final String MIME_FOLDER = "application/vnd.google-apps.folder";

    @Before
    public void setUp() {
        gDrive = new GDrive();
        GoogleTokenResponse resp = new GoogleTokenResponse().setRefreshToken(REFRESH_TOKEN);
        gDrive.initGDrive(USER_ID, DRIVE_ID, resp);
    }

    @Test
    public void testCreateFolder() {
        String folderId = gDrive.createFolder("test", TEST_ROOT_ID);
        File metaData = null;
        try {
            metaData = gDrive.getDrive().files().get(folderId).setFields("name, parents, mimeType").execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        String folderName = metaData.getName();
        String mimeType = metaData.getMimeType();
        String parentId = metaData.getParents().get(0);
        try {
            gDrive.getDrive().files().delete(folderId).execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        assertEquals("test", folderName);
        assertEquals(MIME_FOLDER, mimeType);
        assertEquals(TEST_ROOT_ID, parentId);

    }

    @Test
    public void testSetTrashed() {
        File metaData = null;
        gDrive.setTrashed(TEST_TRASHED_ID, true);
        try {
            metaData = gDrive.getDrive().files().get(TEST_TRASHED_ID).setFields("trashed").execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        boolean isTrashed = metaData.getTrashed();
        gDrive.setTrashed(TEST_TRASHED_ID, false);
        try {
            metaData = gDrive.getDrive().files().get(TEST_TRASHED_ID).setFields("trashed").execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        assertEquals(true, isTrashed);
        assertEquals(false, metaData.getTrashed());

    }
    @Test
    public void testGetListByParentId() {
        List<File> list = gDrive.getListByParentId(TEST_ROOT_ID, false);
        int listSize = list.size();
        String file1Id = null, file2Id = null, file1Name = null, file2Name = null;
        if (listSize == 2) {
            file1Id = list.get(0).getId();
            file1Name = list.get(0).getName();
            file2Id = list.get(1).getId();
            file2Name = list.get(1).getName();
        }
        LOGGER.info(list.toString());
        assertEquals(TEST_FILE_ID, file1Id);
        assertEquals(TEST_FILE_NAME, file1Name);
        assertEquals(TEST_TRASHED_ID, file2Id);
        assertEquals(TEST_TRASHED_NAME, file2Name);

    }
    @Test
    public void testGetParentId() {
        assertEquals(gDrive.getParentId(TEST_FILE_ID),TEST_ROOT_ID);
    }
}
