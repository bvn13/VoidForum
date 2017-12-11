package ru.bvn13.voidforum.controllers.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.bvn13.voidforum.forms.CommentForm;
import ru.bvn13.voidforum.models.Comment;
import ru.bvn13.voidforum.repositories.CommentRepository;
import ru.bvn13.voidforum.services.CommentService;
import ru.bvn13.voidforum.services.PostService;
import ru.bvn13.voidforum.services.UserService;
import ru.bvn13.voidforum.utils.DTOUtil;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by bvn13 on 08.12.2017.
 */
@Controller("accountCommentController")
@RequestMapping("/account/comments")
public class CommentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;



}
