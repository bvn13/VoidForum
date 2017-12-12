package ru.bvn13.voidforum.controllers;

import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bvn13.voidforum.forms.CommentForm;
import ru.bvn13.voidforum.models.Comment;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.SeoPostData;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.bvn13.voidforum.utils.DTOUtil;
import ru.bvn13.voidforum.utils.PaginatorUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.*;


/**
 * bvn13 <mail4bvn@gmail.com>
 */
@Controller
@RequestMapping("posts")
public class PostController {

    Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RequestProcessorService requestProcessorService;

    @Autowired
    private AppSetting appSetting;


    @RequestMapping(value = "archive", method = GET)
    public String archive(Model model){
        model.addAttribute("posts", postService.getArchivePosts());

        return "posts/archive";
    }

    @RequestMapping(value = "{permalink}", method = GET)
    public String show(@PathVariable String permalink, Model model, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
        Post post = this.postService.findPostByPermalink(permalink);
        User user = userService.currentUser();
        Boolean isAdmin = userService.hasPrivilege(user, PrivilegeService.PRIVILEGE_ADMIN);

        if (post.getCensored() && !userService.hasPrivilege(user, PrivilegeService.PRIVILEGE_WRITE)) {
            throw new AccessDeniedException("You are not allowed here");
        }

        logger.debug(String.format("ACCESS %s from IP: %s", permalink, this.requestProcessorService.getRealIp(request)));

        this.visitService.saveVisit(post, this.requestProcessorService.getRealIp(request), this.requestProcessorService.getUserAgent(request));
        post.setVisitsCount(this.visitService.getUniqueVisitsCount(post));
        post.setSympathyCount(this.likeService.getTotalLikesByPost(post));

        SeoPostData seoData = null;
        if (post.getSeoData() == null) {
            seoData = new SeoPostData();
            seoData.setPost(post);
        } else {
            seoData = post.getSeoData();
        }

        model.addAttribute("post", post);
        model.addAttribute("tags", this.postService.getPostTags(post));
        model.addAttribute("seoKeywords", this.postService.getSeoKeywordsAsString(post));
        model.addAttribute("seoDescription", post.getSeoDescription());
        model.addAttribute("seoData", seoData);

        CommentForm commentForm = new CommentForm();
        commentForm.setPostId(post.getId());

        Integer commentsPageSize = appSetting.getCommentsPageSize();
        Integer lastPage = null;
        if (isAdmin) {
            lastPage = commentService.getLastPageCommentsForPostForAdmin(post, commentsPageSize);
        } else {
            lastPage = commentService.getLastPageCommentsForPost(post, commentsPageSize);
        }
        Page<Comment> comments = null;
        if (page > 0) {
            if (isAdmin) {
                comments = commentService.getCommentsForPostForAdmin(post, page - 1, commentsPageSize);
            } else {
                comments = commentService.getCommentsForPost(post, page - 1, commentsPageSize);
            }
        } else {
            page = lastPage+1;
            if (isAdmin) {
                comments = commentService.getCommentsForPostForAdmin(post, lastPage, commentsPageSize);
            } else {
                comments = commentService.getCommentsForPost(post, lastPage, commentsPageSize);
            }
        }

        model.addAttribute("page", page);
        model.addAttribute("pagesList", PaginatorUtil.createPagesList(1, lastPage+1));
        model.addAttribute("totalPages", comments.getTotalPages());
        model.addAttribute("comments", comments);
        model.addAttribute("commentForm", commentForm);
        model.addAttribute("commentFormats", commentService.getAvailableCommentFormats());
        model.addAttribute("disableCommenting", userService.currentUserCanWriteCommentToPost(post));

        return "posts/show";
    }


    @RequestMapping(value = "{permalink}/comments", method = POST)
    public String addComment(@PathVariable String permalink, @Valid CommentForm commentForm, Errors errors, Model model) {

        User user = userService.currentUser();
        Post post = postService.getPost(commentForm.getPostId());

        if (!userService.currentUserCanWrite() || !userService.currentUserCanWriteCommentToPost(post)) {
            throw new AccessDeniedException("You are not allowed here");
        }

        Comment comment = new Comment();
        DTOUtil.mapTo(commentForm, comment);
        comment.setUser(user);
        comment.setPost(post);
        comment.setParentComment(commentService.getCommentById(commentForm.getParentCommentId()));
        comment.setDepth(commentService.calculateDepth(comment));

        commentService.saveComment(comment);

        return "redirect:/posts/"+permalink+"";
    }

}
