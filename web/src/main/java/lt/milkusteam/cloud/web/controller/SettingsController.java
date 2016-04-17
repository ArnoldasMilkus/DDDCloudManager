package lt.milkusteam.cloud.web.controller;

import com.dropbox.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;

/**
 * Created by gediminas on 3/30/16.
 */
@Controller
public class SettingsController {

    Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String showSettings(Model model, Principal principal) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true); // true == allow create

        DbxAppInfo info = new DbxAppInfo("gcx6333853tq53z", "a09ge1pm1sru3ie");
        DbxRequestConfig config = new DbxRequestConfig("JustLearning", Locale.getDefault().toString());
        DbxSessionStore store = new DbxStandardSessionStore(session, "odin");

        DbxWebAuth auth = new DbxWebAuth(config, info, "http://localhost:8080/dbx-auth-finish", store);
        String url = auth.start();
        logger.info("Gautas String: " + url);

        return "settings";
    }

    @RequestMapping(value = "/dbx-auth-finish")
    public String finishDbxAuth(Model model,
                                @RequestParam("state") String state,
                                @RequestParam("code") String code) {
        System.out.println("state = " + state);
        System.out.println("code = " + code);
        return "index";
    }
}
