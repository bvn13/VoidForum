package ru.bvn13.voidforum.controllers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.bvn13.voidforum.error.EmailExistsException;
import ru.bvn13.voidforum.error.NicknameExistsException;
import ru.bvn13.voidforum.forms.RegistrationForm;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.services.UserService;
import ru.bvn13.voidforum.support.web.Message;
import ru.bvn13.voidforum.support.web.MessageHelper;
import ru.bvn13.voidforum.utils.DTOUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

/**
 * bvn13 <mail4bvn@gmail.com>
 */
@Controller(value = "/")
public class UserController {

    @Autowired
    private UserService userService;


    @RequestMapping(value = "signin", method = RequestMethod.GET)
    public String signin(Principal principal, RedirectAttributes ra) {
        return principal == null ? "users/signin" : "redirect:/";
    }


    @GetMapping(value = "register")
    public String registrationForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            // it contains it after post request when errors occurred and was redirected
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "users/register";
    }


    @PostMapping(value = "register")
    public String register(@Valid RegistrationForm registrationForm, Errors errors, Model model, RedirectAttributes ra) {

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

        User user = new User();
        user.setEmail(registrationForm.getUsername());
        user.setNickname(registrationForm.getNickname());
        user.setPassword(registrationForm.getPassword());
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