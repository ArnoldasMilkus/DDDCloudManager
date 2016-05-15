package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.core.dao.VerificationTokenDao;
import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.model.VerificationToken;
import lt.milkusteam.cloud.core.service.UserService;
import lt.milkusteam.cloud.core.validation.RegistrationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import java.util.UUID;

/**
 * Created by gediminas on 3/30/16.
 */
@Controller
public class IndexController {
    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String showIndex() {



            return "index";
    }

}
