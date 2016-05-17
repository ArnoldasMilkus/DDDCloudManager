package lt.milkusteam.cloud.web.controller;

import lt.milkusteam.cloud.core.dao.UserDao;
import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.model.UserDTO;
import lt.milkusteam.cloud.core.model.VerificationToken;
import lt.milkusteam.cloud.core.service.UserService;
import lt.milkusteam.cloud.core.validation.EmailExistsException;
import lt.milkusteam.cloud.core.validation.OnRegistrationCompleteEvent;
import lt.milkusteam.cloud.core.validation.UsernameExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arnoldas on 4/16/16.
 */
@Controller
public class RegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    private final static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private Pattern pattern;

    private Matcher matcher;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserDao userDao;

    @Autowired
    private MessageSource messages;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDTO userDto = new UserDTO();

        model.addAttribute("user", userDto);
        return "registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDTO accountDto,
                                            BindingResult result, WebRequest request, Errors errors) {
        User registered = new User();
        Locale locale = request.getLocale();


        if(!accountDto.validateFields()){
            String message = messages.getMessage("register.all", null, locale);
            ObjectError objectError = new ObjectError("error", message);
            result.addError(objectError);
            return new ModelAndView("registration", "user", accountDto);
        }
        if (!accountDto.getMatchingPassword().equals(accountDto.getPassword())) {
            String message = messages.getMessage("register.psw", null, locale);
            ObjectError objectError = new ObjectError("error", message);
            result.addError(objectError);
            return new ModelAndView("registration", "user", accountDto);
        }
        if (!validateEmail(accountDto.getEmail())) {
            String message = messages.getMessage("register.emailError", null, locale);

            ObjectError objectError = new ObjectError("error", message);
            result.addError(objectError);
            return new ModelAndView("registration", "user", accountDto);
        }


        if (!result.hasErrors()) {
            registered = createUserAccount(accountDto, result);
        }
        if (registered == null) {
            result.rejectValue("email", "message.regError");
            String message = messages.getMessage("register.databaseError", null, locale);

            ObjectError objectError = new ObjectError("error", message);
            result.addError(objectError);
            return new ModelAndView("registration", "user", accountDto);
        }
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", accountDto);
        }
        try {

            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent
                    (registered, request.getLocale(), appUrl));
        } catch (Exception me) {
            LOGGER.error(me.getMessage());
            return new ModelAndView("emailError", "user", accountDto);
        }
        if (result.hasErrors()) {
            return new ModelAndView("registration", "user", accountDto);
        } else {
            return new ModelAndView("successRegistration", "user", accountDto);
        }
    }

    @RequestMapping(value = "/successRegistration", method = RequestMethod.GET)
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token) {
        Locale locale = request.getLocale();
        System.out.println(token);
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            String message = messages.getMessage("register.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser";
        }

        User user = userDao.findByUsername(verificationToken.getUsername());
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", messages.getMessage("register.expireToken", null, locale));
            return "redirect:/badUser";
        }
        user.setEnabled(true);
        userService.saveRegisteredUser(user.getUsername());
        return "redirect:/login";
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
