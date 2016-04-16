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
public class LoginControllerTest extends AbstractIntegrationTest {

    @Test
    public void testGetLoginPage() throws Exception {
        mockMvc.perform(get("/login")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(forwardedUrl("/WEB-INF/pages/login.jsp"));
    }

    @Test
    @WithMockUser
    public void testRedirect() throws Exception {
        mockMvc.perform(get("/login")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        mockMvc.perform(formLogin())
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated());
    }

    @Test
    public void testUnsuccessfulLogin() throws Exception {
        mockMvc.perform(formLogin().password("invalid_password"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

    }

    @Test
    public void testCSRF() throws Exception {
        RequestBuilder requestBuilder = post("/login").with(csrf().useInvalidToken())
                .param("username", "user")
                .param("password", "password");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }
}
