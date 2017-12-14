package ru.bvn13.voidforum.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.bvn13.voidforum.error.EmailExistsException;
import ru.bvn13.voidforum.error.NicknameExistsException;
import ru.bvn13.voidforum.forms.RegistrationForm;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.support.AppLocale;
import ru.bvn13.voidforum.services.UserService;
import ru.bvn13.voidforum.support.localization.SessionStorer;
import ru.bvn13.voidforum.support.web.MessageHelper;

import javax.validation.Valid;
import java.security.Principal;

/**
 * bvn13 <mail4bvn@gmail.com>
 */
@Controller(value = "/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SessionStorer sessionStorer;


    @RequestMapping(value = "signin", method = RequestMethod.GET)
    public String signin(@RequestParam(defaultValue = "en") String lang, Principal principal, RedirectAttributes ra) {
        sessionStorer.setLocale(AppLocale.valueOfOrDefault(lang.toUpperCase()));
        return principal == null ? "users/signin" : "redirect:/";
    }


    @GetMapping(value = "register")
    public String registrationForm(@RequestParam(defaultValue = "en") String lang, Model model) {
        if (!model.containsAttribute("registrationForm")) {
            // it contains it after post request when errors occurred and was redirected
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        sessionStorer.setLocale(AppLocale.valueOfOrDefault(lang.toUpperCase()));
        return "users/register";
    }


    @PostMapping(value = "register")
    public String register(@RequestParam(defaultValue = "en") String lang, @Valid RegistrationForm registrationForm, Errors errors, Model model, RedirectAttributes ra) {

        if (errors.hasErrors()) {
            MessageHelper.addNamedErrorsAsList(ra, "errors", "Please check errors:", errors);
            ra.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }

        if (!registrationForm.getPassword().equals(registrationForm.getPasswordCheck())) {
            MessageHelper.addNamedErrorAttribute(ra, "error", "Verify your password!");
            ra.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }

        if (registrationForm.getUsername().isEmpty()
                || registrationForm.getNickname().isEmpty()
                || registrationForm.getPassword().isEmpty()
                || registrationForm.getPasswordCheck().isEmpty()) {
            MessageHelper.addNamedErrorAttribute(ra, "error", "Not all necessary fields are specified");
            ra.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }

        AppLocale locale = AppLocale.valueOfOrDefault(lang.toUpperCase());

        User user = new User();
        user.setEmail(registrationForm.getUsername());
        user.setNickname(registrationForm.getNickname());
        user.setPassword(registrationForm.getPassword());
        user.setLocale(locale);
        //DTOUtil.mapTo(registrationForm, user);

        try {
            userService.registerNewUserAccount(user);
        } catch (EmailExistsException e) {
            MessageHelper.addNamedErrorAttribute(ra, "error", e.getMessage());
            ra.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        } catch (NicknameExistsException e) {
            MessageHelper.addNamedErrorAttribute(ra, "error", e.getMessage());
            ra.addFlashAttribute("registrationForm", registrationForm);
            return "redirect:/register";
        }

        return "redirect:/signin";
    }
}