package ru.bvn13.voidforum.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by bvn13 on 09.12.2017.
 */
@Controller
@RequestMapping("tests")
public class TestController {

    @GetMapping(value = "/1")
    public String test1(Model model) {


        return "tests/1";
    }

}
