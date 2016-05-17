package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.web.AbstractIntegrationTest;
import org.hamcrest.core.StringStartsWith;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by gediminas on 4/4/16.
 */
@EnableWebSecurity
public class GDriveFilesControllerTest extends AbstractIntegrationTest {

    private final static String TEST_FOLDER_ID = "0B0Wli1HF90SyNTBPcmxQdTJrSEk";
    private final static String TEST_DOWNLOAD_ID = "0B0Wli1HF90SyWG9xVzdHREdUMlU";
    private final static String TEST_FILE_NAME = "test_file.txt";
    private final static String TEST_USERNAME = "test";

    @Before
    @WithMockUser(value = "test")
    public void beforeTest() throws Exception {
        mockMvc.perform(get("/GDriveFiles"));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testShowFilesSuccess() throws Exception {
        mockMvc.perform(get("/GDriveFiles")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().attribute("driveAuth", true))
                .andExpect(model().attributeExists("files"))
                .andExpect(model().attribute("curId", "root"))
                .andExpect(view().name("GDriveFiles"))
                .andExpect(forwardedUrl("/WEB-INF/pages/GDriveFiles.jsp"));
    }

    @Test
    @WithMockUser
    public void testShowFilesFail() throws Exception {
        mockMvc.perform(get("/GDriveFiles")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().attribute("driveAuth", false))
                .andExpect(model().attributeDoesNotExist("files"))
                .andExpect(view().name("GDriveFiles"))
                .andExpect(forwardedUrl("/WEB-INF/pages/GDriveFiles.jsp"));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testUploadFileSuccess() throws Exception {
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("file", TEST_FILE_NAME, "text/plain", "testUploadFile() integration test".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/GDriveUpload").file(mockMultipartFile).param("parentId", TEST_FOLDER_ID).with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/GDriveFiles?rootId=" + TEST_FOLDER_ID));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testDownloadSuccess() throws Exception {
        mockMvc.perform(get("/GDriveFiles/download").param("fileId", TEST_DOWNLOAD_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    @WithMockUser
    public void testStartAuth() throws Exception {
        mockMvc.perform(get("/GDriveFiles/startAuth"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(StringStartsWith.startsWith("redirect:https://accounts.google.com/o/oauth2/auth")));
    }
}

