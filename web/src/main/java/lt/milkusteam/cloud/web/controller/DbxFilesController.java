package lt.milkusteam.cloud.web.controller;

import com.dropbox.core.v2.files.Metadata;
import lt.milkusteam.cloud.core.model.DbxToken;
import lt.milkusteam.cloud.core.service.DbxFileService;
import lt.milkusteam.cloud.core.service.DbxTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

/**
 * Created by gediminas on 4/17/16.
 */
@Controller
public class DbxFilesController {

    @Autowired
    DbxFileService dbxFileService;

    @Autowired
    DbxTokenService dbxTokenService;

    @RequestMapping(value = "/files")
    public String showFiles(Model model, Principal principal,
                            @RequestParam(name = "path", required = false) String path) {
        String username = principal.getName();
        if (!dbxFileService.contains(username)) {
            DbxToken token = dbxTokenService.findByUsername(username);
            if (token == null) {
                return "redirect:/settings";
            }
            dbxFileService.addClient(username);
        }
        List<Metadata> files = dbxFileService.getFiles(username, path);
        model.addAttribute("files", files);
        return "files";
    }
}

