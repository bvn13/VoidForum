package ru.bvn13.voidforum.controllers;

import ru.bvn13.voidforum.forms.CommentForm;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.SeoPostData;
import ru.bvn13.voidforum.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

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
    private VisitService visitService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RequestProcessorService requestProcessorService;


    @RequestMapping(value = "archive", method = GET)
    public String archive(Model model){
        model.addAttribute("posts", postService.getArchivePosts());

        return "posts/archive";
    }

    @RequestMapping(value = "{permalink}", method = GET)
    public String show(@PathVariable String permalink, Model model, HttpServletRequest request){
        Post post = this.postService.findPostByPermalink(permalink);

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

        model.addAttribute("comments", commentService.getCommentsForPost(post));
        model.addAttribute("commentForm", new CommentForm());
        model.addAttribute("commentFormats", commentService.getAvailableCommentFormats());

        return "posts/show";
    }


}
