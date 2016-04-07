package lt.milkusteam.cloud.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by gediminas on 3/30/16.
 */
@Controller
//@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping(value = "/secret")
public class SecretController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showSecretPage() {
        return "secret";
    }
}
