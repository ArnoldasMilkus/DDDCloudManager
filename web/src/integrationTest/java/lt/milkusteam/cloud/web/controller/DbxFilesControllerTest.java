package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.web.AbstractIntegrationTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by gediminas on 4/4/16.
 */
@EnableWebSecurity
public class DbxFilesControllerTest extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    public void testError() throws Exception {
        mockMvc.perform(get("/dbx/error")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("dbx-error"))
                .andExpect(forwardedUrl("/WEB-INF/pages/dbx-error.jsp"));
    }
}

