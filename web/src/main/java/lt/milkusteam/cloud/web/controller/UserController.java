package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by gediminas on 3/30/16.
 */
@Controller
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String showUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }
}
