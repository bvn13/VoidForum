package ru.bvn13.voidforum.controllers.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.bvn13.voidforum.forms.UserForm;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.repositories.UserRepository;
import ru.bvn13.voidforum.services.UserService;
import ru.bvn13.voidforum.support.web.MessageHelper;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * bvn13 <mail4bvn@gmail.com>.
 */
@Controller("accountUserController")
@RequestMapping("account/profile")
public class UserController {

    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository){
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @RequestMapping()
    public String profile(Model model){
        model.addAttribute("user", userService.currentUser());

        return "account/users/profile";
    }

    @RequestMapping(value = "{userId:[0-9]+}", method = POST)
    public String update(@PathVariable Long userId, @Valid UserForm userForm, Errors errors, RedirectAttributes ra){
        User user = userRepository.findOne(userId);
        Assert.notNull(user);

        if (!userService.currentUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not allowed here");
        }

        if (errors.hasErrors()){
            // do something

            return "account/users/profile";
        }

        if (!userForm.getNewPassword().isEmpty()){

            if (!userService.changePassword(user, userForm.getPassword(), userForm.getNewPassword()))
                MessageHelper.addErrorAttribute(ra, "Change password failed.");
            else
                MessageHelper.addSuccessAttribute(ra, "Change password successfully.");

        }

        return "redirect:/account/profile";
    }
}
