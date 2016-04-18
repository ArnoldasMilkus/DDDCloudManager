package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.core.service.DbxAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

/**
 * Created by gediminas on 3/30/16.
 */
@Controller
public class SettingsController {
    @Autowired
    private DbxAuthService dbxAuthService;

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String showSettings(Model model, Principal principal) {
        return "settings";
    }
}
