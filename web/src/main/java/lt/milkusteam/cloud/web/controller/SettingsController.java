package lt.milkusteam.cloud.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by gediminas on 3/30/16.
 */
@Controller
public class SettingsController {

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    public String showIndex(){
        return "settings";
    }
}
