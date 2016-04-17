package lt.milkusteam.cloud.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Created by gediminas on 4/17/16.
 */
@Controller
public class DbxFilesController {

    @RequestMapping(value = "/files")
    public String showFiles(Model model, Principal principal) {

        return files;
    }
}

