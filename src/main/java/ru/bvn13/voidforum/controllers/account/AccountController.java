package ru.bvn13.voidforum.controllers.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by bvn13 on 08.12.2017.
 */
@Controller
@RequestMapping(value = "/account")
public class AccountController {

    @GetMapping(value = "")
    public String getAccountIndex(Model model) {

        return "account/home/index";

    }

}
