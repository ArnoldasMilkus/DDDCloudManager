package lt.milkusteam.cloud.core.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.drive.model.File;
import lt.milkusteam.cloud.core.GDriveAPI.GDrive;
import lt.milkusteam.cloud.core.GDriveAPI.GDriveUploader;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vilintas on 2016-05-18.
 */
public class GDriveUploaderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDriveUploaderTest.class);
    private GDrive gDrive;

    private static final String USER_ID = "vilstr32";
    private static final String DRIVE_ID = "001";
    private static final String REFRESH_TOKEN = "1/e5SMK2LXZn2Y3Pt1h4nEo49KlVaR58NGXZWetVbnmqE";
    private static final String TEST_ROOT_ID = "0B2iI6jzmh9j_dDQtTlBTWndKNEU";
    private static final String TEST_UPLOAD_FILE_NAME = "TestUpload.txt";
    private static final long TEST_FILE_SIZE = 10; // in bytes

    @Before
    public void setUp() {
        gDrive = new GDrive();
        GoogleTokenResponse resp = new GoogleTokenResponse().setRefreshToken(REFRESH_TOKEN);
        gDrive.initGDrive(USER_ID, DRIVE_ID, resp);
    }

    @Test
    public void testSimpleUploadStream() {
        java.io.File file = new java.io.File(TEST_UPLOAD_FILE_NAME);
        File metadata = new File().setName(TEST_UPLOAD_FILE_NAME).setParents(Arrays.asList(TEST_ROOT_ID));
        InputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
            File upFile = new GDriveUploader().simpleUploadStream(gDrive.getDrive(), metadata, inStream, true);
            metadata = gDrive.getDrive().files().get(upFile.getId()).setFields("name, size, parents, id").execute();
            gDrive.getDrive().files().delete(metadata.getId()).execute();
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        assertEquals(TEST_UPLOAD_FILE_NAME, metadata.getName());
        assertEquals(TEST_FILE_SIZE, (long) metadata.getSize());
        assertEquals(TEST_ROOT_ID, metadata.getParents().get(0));
    }
}
