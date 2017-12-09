package ru.bvn13.voidforum.controllers;

import ru.bvn13.voidforum.forms.LikeForm;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.services.*;
import ru.bvn13.voidforum.support.web.ViewHelperVF;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@Controller
@RequestMapping(value = "/sympathy")
public class SympathyController {

    @Autowired
    private AppSetting appSetting;

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestProcessorService requestProcessorService;


    @Data
    public static class SympathyRequestData {
        @NotNull
        private String postId;
    }


    @PostMapping(value = "/like")
    public @ResponseBody
    LikeForm likeIt(@RequestBody SympathyRequestData data, HttpServletRequest request) {
        Post post = this.postService.findPostByPermalink(data.getPostId());
        this.likeService.likePost(post, this.requestProcessorService.getRealIp(request));
        ViewHelperVF viewHelper = new ViewHelperVF(this.appSetting);
        LikeForm result = new LikeForm();
        result.setSympathy(viewHelper.formatNumberByThousands(this.likeService.getTotalLikesByPost(post)));
        return result;
    }

    @PostMapping(value = "/dislike")
    public @ResponseBody
    LikeForm dislikeIt(@RequestBody SympathyRequestData data, HttpServletRequest request) {
        Post post = this.postService.findPostByPermalink(data.getPostId());
        this.likeService.dislikePost(post, this.requestProcessorService.getRealIp(request));
        ViewHelperVF viewHelper = new ViewHelperVF(this.appSetting);
        LikeForm result = new LikeForm();
        result.setSympathy(viewHelper.formatNumberByThousands(this.likeService.getTotalLikesByPost(post)));
        return result;
    }



}
