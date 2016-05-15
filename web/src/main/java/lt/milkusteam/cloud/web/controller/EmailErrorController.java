package lt.milkusteam.cloud.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by Arnoldas on 2016.05.14.
 */
@Controller
public class EmailErrorController {

    @RequestMapping(value = "/emailError", method = RequestMethod.GET)
    public String showIndex() {
        return "emailError";
    }
}
