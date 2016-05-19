package lt.milkusteam.cloud.core.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.model.File;
import lt.milkusteam.cloud.core.GDriveAPI.GDrive;
import lt.milkusteam.cloud.core.dao.GDriveTokenDAO;
import lt.milkusteam.cloud.core.dao.impl.GDriveTokenDAOImpl;
import lt.milkusteam.cloud.core.model.GDriveToken;
import lt.milkusteam.cloud.core.service.impl.GDriveFilesServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * Created by gediminas on 3/30/16.
 */
public class GDriveFilesServiceImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GDriveFilesServiceImplTest.class);

    private static final String USER_ID = "vilstr32";
    private static final String NON_USER_ID = "neegzistuoja";
    private static final String DRIVE_ID = "001";
    private static final String REFRESH_TOKEN = "1/e5SMK2LXZn2Y3Pt1h4nEo49KlVaR58NGXZWetVbnmqE";
    private static final String TEST_ROOT_ID = "0B2iI6jzmh9j_dDQtTlBTWndKNEU";
    private static final String TEST_FILE_ID = "0B2iI6jzmh9j_Zko0Qk55Tl9BVkE";
    private static final String TEST_TRASHED_ID = "0B2iI6jzmh9j_M0dlWmlzN2JmZE0";
    private static final String TEST_FILE_NAME = "ForUnitTest.txt";
    private static final String TEST_TRASHED_NAME = "Trash.txt";
    private static final String TEST_UPLOAD_FILE_NAME = "TestUpload.txt";
    private static final long TEST_FILE_SIZE = 10; // in bytes
    private static final String MIME_FOLDER = "application/vnd.google-apps.folder";
    private static final String MIME_FILE = "application/octet-stream";
    private static final int INDEX = 0;

    @Autowired
    HttpServletResponse servletResponse;

    private GDriveFilesService gDriveFilesService;
    private GDrive drive;

    @Before
    public void setUp() {
        gDriveFilesService = new GDriveFilesServiceImpl();
        HashMap<String, List<GDrive>> driveMap = new HashMap<>();
        drive = new GDrive();
        GoogleTokenResponse resp = new GoogleTokenResponse().setRefreshToken(REFRESH_TOKEN);
        drive.initGDrive(USER_ID, DRIVE_ID, resp);
        driveMap.put(USER_ID, Arrays.asList(drive));
        setField(gDriveFilesService, "driveMap", driveMap);
        GDriveTokenDAO gDriveTokenDAO = mock(GDriveTokenDAOImpl.class);
        when(gDriveTokenDAO.findByUsername(NON_USER_ID)).thenReturn(new GDriveToken(NON_USER_ID, REFRESH_TOKEN));
        setField(gDriveFilesService, "gDriveTokenDAO", gDriveTokenDAO);
    }

    @Test
    public void testFindAllInDirectory() {
        List<File> list = gDriveFilesService.findAllInDirectory(TEST_ROOT_ID, USER_ID, false, INDEX);
        int listSize = list.size();
        String file1Id = null, file2Id = null, file1Name = null, file2Name = null;
        if (listSize == 2) {
            file1Id = list.get(0).getId();
            file1Name = list.get(0).getName();
            file2Id = list.get(1).getId();
            file2Name = list.get(1).getName();
        }
        assertEquals(TEST_FILE_ID, file1Id);
        assertEquals(TEST_FILE_NAME, file1Name);
        assertEquals(TEST_TRASHED_ID, file2Id);
        assertEquals(TEST_TRASHED_NAME, file2Name);

    }

    @Test
    public void testGetIfChild() {
        String parentId = gDriveFilesService.getIfChild(TEST_FILE_ID, USER_ID, INDEX);
        assertEquals(TEST_ROOT_ID, parentId);

    }

    @Test
    public void testUploadFile() {
        java.io.File file = new java.io.File(TEST_UPLOAD_FILE_NAME);
        InputStream inStream = null;
        File metadata = null;
        try {
            inStream = new FileInputStream(file);
            File upFile = gDriveFilesService.uploadFile(inStream, TEST_ROOT_ID, TEST_UPLOAD_FILE_NAME, USER_ID, false, 0);
            metadata = drive.getDrive().files().get(upFile.getId()).setFields("name, size, parents, id").execute();
            drive.getDrive().files().delete(upFile.getId()).execute();
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        assertEquals(TEST_UPLOAD_FILE_NAME, metadata.getName());
        assertEquals(TEST_FILE_SIZE, (long) metadata.getSize());
        assertEquals(TEST_ROOT_ID, metadata.getParents().get(0));

    }

    @Test
    public void testAddRemoveContainsClient() {
        boolean isContaining = gDriveFilesService.containsClient(USER_ID, INDEX);
        assertEquals(true, isContaining);

        isContaining = gDriveFilesService.containsClient(NON_USER_ID, INDEX);
        assertEquals(false, isContaining);

        gDriveFilesService.addClient(NON_USER_ID);
        isContaining = gDriveFilesService.containsClient(NON_USER_ID, INDEX);
        assertEquals(true, isContaining);

        gDriveFilesService.removeClient(NON_USER_ID, INDEX);
        isContaining = gDriveFilesService.containsClient(NON_USER_ID, INDEX);
        assertEquals(false, isContaining);

        isContaining = gDriveFilesService.containsClient(USER_ID, INDEX);
        assertEquals(true, isContaining);
    }

    @Test
    public void testDownloadToClient() {
        servletResponse = new MockHttpServletResponse();
        gDriveFilesService.downloadToClient(servletResponse, TEST_FILE_ID, USER_ID, INDEX);
        assertEquals(MIME_FILE, servletResponse.getContentType());
    }

    @Test
    public void testFixTrashed() {
        gDriveFilesService.fixTrashed(USER_ID, INDEX, true, TEST_TRASHED_ID);
        boolean isTrashed = false;
        try {
            isTrashed = drive.getDrive().files().get(TEST_TRASHED_ID).setFields("trashed").execute().getTrashed();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        assertEquals(true, isTrashed);
        gDriveFilesService.fixTrashed(USER_ID, INDEX, false, TEST_TRASHED_ID);
        try {
            isTrashed = drive.getDrive().files().get(TEST_TRASHED_ID).setFields("trashed").execute().getTrashed();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        assertEquals(false, isTrashed);
    }

    @Test
    public void testNewFolder() {
        String folderId = gDriveFilesService.newFolder(USER_ID, INDEX, "test", TEST_ROOT_ID);
        File metaData = null;
        try {
            metaData = drive.getDrive().files().get(folderId).setFields("name, parents, mimeType").execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        String folderName = metaData.getName();
        String mimeType = metaData.getMimeType();
        String parentId = metaData.getParents().get(0);
        try {
            drive.getDrive().files().delete(folderId).execute();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        assertEquals("test", folderName);
        assertEquals(MIME_FOLDER, mimeType);
        assertEquals(TEST_ROOT_ID, parentId);
    }

    @Test
    public void testReturnStream() {
        InputStream inputStream = gDriveFilesService.returnStream(USER_ID, INDEX, TEST_FILE_ID);
        File fileMetadata = new File();
        File originalMetadata = null;
        List<String> parents = new ArrayList<>();
        parents.add(TEST_ROOT_ID);
        fileMetadata.setParents(parents);
        fileMetadata.setName("stream");
        fileMetadata.setMimeType(MIME_FILE);
        InputStreamContent astr = new InputStreamContent("", inputStream);
        File upFile = null;
        try {
            upFile = drive.getDrive().files().create(fileMetadata, astr).execute();
            fileMetadata = drive.getDrive().files().get(upFile.getId()).setFields("name, size, mimeType, md5Checksum").execute();
            originalMetadata = drive.getDrive().files().get(TEST_FILE_ID).setFields("name, size, mimeType, md5Checksum").execute();
            drive.getDrive().files().delete(upFile.getId()).execute();
            inputStream.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        assertEquals(originalMetadata.getSize(), fileMetadata.getSize());
        assertEquals(originalMetadata.getMimeType(), fileMetadata.getMimeType());
        assertEquals(originalMetadata.getMd5Checksum(), fileMetadata.getMd5Checksum());
        assertEquals("stream", fileMetadata.getName());
    }

    @Test
    public void testGetName() {
        String fileName = gDriveFilesService.getName(USER_ID, INDEX, TEST_FILE_ID);
        String trulyFileName = null;
        try {
            trulyFileName = drive.getDrive().files().get(TEST_FILE_ID).setFields("name").execute().getName();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        assertEquals(trulyFileName, fileName);
    }


    @Test
    public void testGetSize() {
        long fileSize = gDriveFilesService.getSize(USER_ID, INDEX, TEST_FILE_ID);
        long trulyFileSize = 0;
        try {
            trulyFileSize = drive.getDrive().files().get(TEST_FILE_ID).setFields("size").execute().getSize();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        assertEquals(trulyFileSize, fileSize);
    }
}
