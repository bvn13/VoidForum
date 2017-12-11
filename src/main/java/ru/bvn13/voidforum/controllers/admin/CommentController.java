package ru.bvn13.voidforum.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bvn13.voidforum.forms.CommentDeletionForm;
import ru.bvn13.voidforum.models.Comment;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.services.CommentService;
import ru.bvn13.voidforum.services.PostService;

import javax.validation.Valid;

import java.nio.file.AccessDeniedException;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by bvn13 on 11.12.2017.
 */
@Controller("adminCommentController")
@RequestMapping("/admin/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;


    @RequestMapping(value = "/{commentId:[\\d]+}/delete", method = {POST})
    public String deleteComment(@PathVariable Long commentId, @Valid CommentDeletionForm form, Errors errors, Model model) throws Exception {
        if (errors.hasErrors()) {
            throw new Exception("Wrong request");
        }
        Comment comment = commentService.getCommentById(commentId);

        if (!comment.getPost().getId().equals(form.getPostId())) {
            throw new AccessDeniedException("Comment "+commentId+" does not belong to post "+form.getPostId());
        }

        commentService.deleteComment(commentId);
        return "redirect:/posts/"+form.getPostId();
    }

}
