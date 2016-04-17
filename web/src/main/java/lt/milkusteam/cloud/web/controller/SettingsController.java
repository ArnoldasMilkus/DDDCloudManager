package lt.milkusteam.cloud.web.controller;

import com.dropbox.core.*;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.service.DbxTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by gediminas on 3/30/16.
 */
@Controller
public class SettingsController {
    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    private HashMap<String, DbxWebAuth> webAuths = new HashMap<>();

    @Autowired
    private DbxTokenService dbxTokenService;


    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String showSettings(Model model, Principal principal) {

        String username = principal.getName();
        model.addAttribute("dbxAuth", dbxTokenService.findByUsername(username) != null);
        return "settings";
    }

    @RequestMapping(value = "/dbx-auth-start", method = RequestMethod.POST)
    public String startDbxAuth(Model model, Principal principal) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);

        DbxAppInfo info = new DbxAppInfo("gcx6333853tq53z", "a09ge1pm1sru3ie");
        DbxRequestConfig config = new DbxRequestConfig("JustLearning", Locale.getDefault().toString());
        DbxSessionStore store = new DbxStandardSessionStore(session, "odin");

        DbxWebAuth auth = new DbxWebAuth(config, info, "http://localhost:8080/dbx-auth-finish", store);

        webAuths.put(principal.getName(), auth);
        String authUrl = auth.start();
        logger.info("Dropbox authentication started", authUrl);
        return "redirect:" + authUrl;
    }

    @RequestMapping(value = "/dbx-auth-finish")
    public String finishDbxAuth(Model model, Principal principal,
                                @RequestParam("state") String state,
                                @RequestParam("code") String code) {
        DbxWebAuth webAuth = webAuths.get(principal.getName());
        if (webAuth == null) {
            System.out.println("WEB AUTH NOT FOUND");
            return "index";
        }
        Map<String, String[]> params = new HashMap<>();
        params.put("state", new String[] { state });
        params.put("code", new String[] { code });
        DbxAuthFinish authResult = null;
        try {
            authResult = webAuth.finish(params);
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (DbxWebAuth.BadRequestException e) {
            e.printStackTrace();
        } catch (DbxWebAuth.BadStateException e) {
            e.printStackTrace();
        } catch (DbxWebAuth.CsrfException e) {
            e.printStackTrace();
        } catch (DbxWebAuth.NotApprovedException e) {
            e.printStackTrace();
        } catch (DbxWebAuth.ProviderException e) {
            e.printStackTrace();
        }

        if (authResult != null) {
            DbxToken token = new DbxToken(principal.getName(), authResult.getAccessToken());
            dbxTokenService.save(token);
            logger.info("User " + principal.getName() + " dropbox authentication success.");
        } else {
            logger.error("User " + principal.getName() + " Dropbox authentication failure.");
        }

        webAuths.remove(principal.getName());

        return "index";
    }

    @RequestMapping(value = "/dbx-auth-clear", method = RequestMethod.POST)
    public String clearDbxAuth(Principal principal) {
        dbxTokenService.delete(principal.getName());
        logger.info("User " + principal.getName() + " dropbox access token delete.");
        return "index";
    }
}
