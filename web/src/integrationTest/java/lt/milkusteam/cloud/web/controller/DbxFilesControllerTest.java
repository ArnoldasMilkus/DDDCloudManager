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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by gediminas on 4/4/16.
 */
@EnableWebSecurity
public class DbxFilesControllerTest extends AbstractIntegrationTest {

    private final static String TEST_PATH = "/DDDCloudManagerTest";
    private final static String TEST_DOWNLOAD_PATH = "/DDDCloudManagerTest/download_test.jpeg";
    private final static String TEST_FILE_NAME = "test_file.txt";
    private final static String TEST_NONEXISTANT_FILE_NAME = "surely_this_file_does_not_exist.txt";
    private final static String TEST_FOLDER_NAME = "test_folder";
    private final static String TEST_USERNAME = "test";

    @Before
    @WithMockUser(value = "test")
    public void beforeTest() throws Exception {
        mockMvc.perform(get("/dbx/"));
    }

    @Test
    @WithMockUser
    public void testShowFilesError() throws Exception {
        mockMvc.perform(get("/dbx/files")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", 1))
                .andExpect(view().name("dbx-error"))
                .andExpect(forwardedUrl("/WEB-INF/pages/dbx-error.jsp"));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testShowFiles() throws Exception {
        mockMvc.perform(get("/dbx/files")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("files"))
                .andExpect(model().attributeExists("folders"))
                .andExpect(model().attributeExists("spaceUsage"))
                .andExpect(view().name("dbx-files"))
                .andExpect(forwardedUrl("/WEB-INF/pages/dbx-files.jsp"));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testShowDeletedFiles() throws Exception {
        mockMvc.perform(get("/dbx/trash")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("files"))
                .andExpect(model().attributeDoesNotExist("folders"))
                .andExpect(view().name("dbx-deleted-files"))
                .andExpect(forwardedUrl("/WEB-INF/pages/dbx-deleted-files.jsp"));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testUploadFile() throws Exception {
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("file", TEST_FILE_NAME, "text/plain", "testUploadFile() integration test".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/dbx/upload").file(mockMultipartFile).param("path", TEST_PATH).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "uploadSuccess"))
                .andExpect(view().name("redirect:/dbx/files?path=" + TEST_PATH))
                .andExpect(redirectedUrl("/dbx/files?path=" + TEST_PATH));
        mockMvc.perform(get("/dbx/delete").param("path", TEST_PATH + "/" + TEST_FILE_NAME));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testCreateFolderSuccess() throws Exception {
        mockMvc.perform(post("/dbx/create")
                .with(csrf()).param("path", TEST_PATH).param("name", TEST_FOLDER_NAME))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "createSuccess"))
                .andExpect(view().name("redirect:/dbx/files?path=" + TEST_PATH))
                .andExpect(redirectedUrl("/dbx/files?path=" + TEST_PATH));
        mockMvc.perform(get("/dbx/delete").param("path", TEST_PATH + "/" + TEST_FOLDER_NAME));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testCreateFolderFail() throws Exception {
        mockMvc.perform(post("/dbx/create")
                .with(csrf()).param("path", TEST_PATH).param("name", TEST_FOLDER_NAME));
        mockMvc.perform(post("/dbx/create")
                .with(csrf()).param("path", TEST_PATH).param("name", TEST_FOLDER_NAME))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", 0))
                .andExpect(view().name("dbx-error"))
                .andExpect(forwardedUrl("/WEB-INF/pages/dbx-error.jsp"));
        mockMvc.perform(get("/dbx/delete").param("path", TEST_PATH + "/" + TEST_FOLDER_NAME));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testDeleteSuccess() throws Exception {
        // creating a file to delete
        mockMvc.perform(post("/dbx/create").with(csrf()).param("path", TEST_PATH).param("name", TEST_FILE_NAME));
        mockMvc.perform(get("/dbx/delete")
                .param("path", TEST_PATH + "/" + TEST_FILE_NAME))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "removeSuccess"))
                .andExpect(view().name("redirect:/dbx/files?path=" + TEST_PATH))
                .andExpect(redirectedUrl("/dbx/files?path=" + TEST_PATH));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testDeleteFail() throws Exception {
        mockMvc.perform(get("/dbx/delete")
                .param("path", TEST_PATH + "/" + TEST_NONEXISTANT_FILE_NAME))
                .andExpect(model().attribute("error", 0))
                .andExpect(view().name("dbx-error"))
                .andExpect(forwardedUrl("/WEB-INF/pages/dbx-error.jsp"));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testRestoreSuccess() throws Exception {
        mockMvc.perform(post("/dbx/create").with(csrf()).param("path", TEST_PATH).param("name", TEST_FILE_NAME));
        mockMvc.perform(get("/dbx/delete").param("path", TEST_PATH + "/" + TEST_FILE_NAME));
        mockMvc.perform(get("/dbx/restore")
                .param("path", TEST_PATH + "/" + TEST_FILE_NAME))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", "restoreSuccess"))
                .andExpect(view().name("redirect:/dbx/files?path=" + TEST_PATH))
                .andExpect(redirectedUrl("/dbx/files?path=" + TEST_PATH));
        mockMvc.perform(get("/dbx/delete").param("path", TEST_PATH + "/" + TEST_FILE_NAME));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testRestoreFail() throws Exception {
        mockMvc.perform(get("/dbx/restore")
                .param("path", TEST_PATH + "/" + TEST_NONEXISTANT_FILE_NAME))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", 0))
                .andExpect(view().name("dbx-error"))
                .andExpect(forwardedUrl("/WEB-INF/pages/dbx-error.jsp"));
        mockMvc.perform(get("/dbx/delete").param("path", TEST_PATH + "/" + TEST_FOLDER_NAME));
    }

    @Test
    @WithMockUser
    public void testStartAuth() throws Exception {
        mockMvc.perform(post("/dbx/auth-start").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(StringStartsWith.startsWith("redirect:https://www.dropbox.com/1/oauth2/authorize")));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testDownloadSuccess() throws Exception {
        mockMvc.perform(get("/dbx/download").param("path", TEST_DOWNLOAD_PATH))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"));
    }

    @Test
    @WithMockUser(TEST_USERNAME)
    public void testDownloadFail() throws Exception {
        mockMvc.perform(get("/dbx/download").param("path", TEST_PATH + "/" + TEST_NONEXISTANT_FILE_NAME))
                .andExpect(status().isOk())
                .andExpect(model().attribute("error", 0))
                .andExpect(view().name("dbx-error"))
                .andExpect(forwardedUrl("/WEB-INF/pages/dbx-error.jsp"));
    }

}

