package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.core.service.DbxFileService;
import lt.milkusteam.cloud.core.service.GDriveFilesService;
import lt.milkusteam.cloud.core.service.GDriveOAuth2Service;
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

    @Autowired
    GDriveOAuth2Service gDriveOAuth2Service;

    @Autowired
    GDriveFilesService gDriveFilesService;

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String showSettings(Model model, Principal principal) {
        String userName = principal.getName();
        if (dbxFileService.containsClient(userName) || dbxFileService.addClient(userName)) {
            String dbxAccountDetails = dbxFileService.getAccountInfo(userName);
            model.addAttribute("dbxAccount", dbxAccountDetails);
        }
        if (gDriveOAuth2Service.isLinked(userName)) {
            String gDriveAccountDetails = gDriveFilesService.getEmail(userName, 0);
            model.addAttribute("gDriveAccount", gDriveAccountDetails);
        }
        return "settings";
    }
}
