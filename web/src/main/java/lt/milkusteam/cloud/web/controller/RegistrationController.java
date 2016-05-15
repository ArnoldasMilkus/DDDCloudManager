package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.model.UserDTO;
import lt.milkusteam.cloud.core.service.UserService;
import lt.milkusteam.cloud.core.validation.EmailExistsException;
import lt.milkusteam.cloud.core.validation.UsernameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arnoldas on 4/16/16.
 */
@Controller
public class RegistrationController {
    Pattern pattern;
    Matcher matcher;
    final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        //UserDto userDto = new UserDto();
        UserDTO userDto = new UserDTO();

        model.addAttribute("user", userDto);
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDTO accountDto,
                                            BindingResult result, WebRequest request, Errors errors) {
        User registered = new User();

        if(accountDto.getMatchingPassword() == "" || accountDto.getUsername() == "" || accountDto.getEmail() == "" ||
                accountDto.getFirstName()== "" || accountDto.getLastName() == "" || accountDto.getPassword() ==""){
            ObjectError objectError = new ObjectError("error", "Visi laukai turi būti užpildyti ");
            result.addError(objectError);
            return new ModelAndView("registration", "user", accountDto);
        }
        if (!accountDto.getMatchingPassword().equals(accountDto.getPassword())) {

            ObjectError objectError = new ObjectError("error", "passswords don't match");
            result.addError(objectError);
            return new ModelAndView("registration", "user", accountDto);
        }
        if (!validateEmail(accountDto.getEmail())) {
            ObjectError objectError = new ObjectError("error", "email is not right written");
            result.addError(objectError);
            return new ModelAndView("registration", "user", accountDto);
        }


        if (!result.hasErrors()) {
            registered = createUserAccount(accountDto, result);
        }
        if (registered == null) {
            ///result.rejectValue("email", "message.regError");
            ObjectError objectError = new ObjectError("error", "email or username is in the database");
            result.addError(objectError);
            return new ModelAndView("registration", "user", accountDto);
        }
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", accountDto);
        } else {
            return new ModelAndView("successRegister", "user", accountDto);
        }
    }

    private User createUserAccount(UserDTO accountDto, BindingResult result) {
        User registered = null;
        try {
            registered = userService.registerNewUserAccount(accountDto);
        } catch (EmailExistsException e) {
            return null;
        } catch (UsernameExistsException e1) {
            return null;
        }

        return registered;
    }

    private boolean validateEmail(final String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
