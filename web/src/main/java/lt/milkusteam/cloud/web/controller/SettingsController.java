package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.core.service.DbxFileService;
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
    DbxFileService dbxFileService;

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String showSettings(Model model, Principal principal) {
        if (dbxFileService.containsClient(principal.getName()) || dbxFileService.addClient(principal.getName())) {
            String dbxAccountDetails = dbxFileService.getAccountInfo(principal.getName());
            model.addAttribute("dbxAccount", dbxAccountDetails);
        }
        return "settings";
    }
}
