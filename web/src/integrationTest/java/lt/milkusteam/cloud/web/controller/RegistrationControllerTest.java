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
public class RegistrationControllerTest extends AbstractIntegrationTest {

    @Test
    public void testGetRegistrationForm() throws Exception {
        mockMvc.perform(get("/registration")
                .accept(MediaType.TEXT_HTML))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("registration"))
                .andExpect(forwardedUrl("/WEB-INF/pages/registration.jsp"));
    }

    @Test
    public void testRegistrationConfirmValidToken() throws Exception {
        mockMvc.perform(get("/successRegistration?token=testToken")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(model().attributeDoesNotExist("message"))
                .andExpect(redirectedUrl("/login"));
    }
    @Test
    public void testRegistrationConfirmInvalidToken() throws Exception {
        mockMvc.perform(get("/successRegistration?token=invalidToken")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/badUser"))
                .andExpect(model().attributeExists("message"))
                .andExpect(redirectedUrl("/badUser?message=Token+is+invalid"));
    }
    @Test
    public void testRegistrationConfirmExpiredToken() throws Exception {
        mockMvc.perform(get("/successRegistration?token=expiredToken")
                .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/badUser"))
                .andExpect(model().attributeExists("message"))
                .andExpect(redirectedUrl("/badUser?message=Token+is+expired"));
    }
}
