package ru.bvn13.voidforum.controllers.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by bvn13 on 08.12.2017.
 */
@Controller("accountCommentController")
@RequestMapping("/account/comments")
public class CommentController {


    @RequestMapping(value = "", method = POST)
    public String addComment(Model model) {

        return "";
    }

}
