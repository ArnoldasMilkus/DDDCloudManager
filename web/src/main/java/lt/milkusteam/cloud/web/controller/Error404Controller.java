package lt.milkusteam.cloud.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by tombru on 2016.04.20.
 */
@Controller
public class Error404Controller {

    @RequestMapping(value = "/error404", method = RequestMethod.GET)
    public String showIndex(){
        return "error404";
    }
}