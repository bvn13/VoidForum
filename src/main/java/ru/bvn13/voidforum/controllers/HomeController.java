package ru.bvn13.voidforum.controllers;

import ru.bvn13.voidforum.Constants;
import ru.bvn13.voidforum.error.NotFoundException;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.services.AppSetting;
import ru.bvn13.voidforum.services.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bvn13.voidforum.services.PrivilegeService;
import ru.bvn13.voidforum.services.UserService;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppSetting appSetting;

    @RequestMapping(value = "", method = GET)
    public String index(@RequestParam(defaultValue = "1") int page, Model model, HttpServletRequest request) {
        //request.isUserInRole("READ_PRIVILEGE");
        page = page < 1 ? 0 : page - 1;
        Page<Post> posts = null;
        if (userService.currentUserHasPrivilege(PrivilegeService.PRIVILEGE_WRITE)) {
            if (userService.currentUserHasPrivilege(PrivilegeService.PRIVILEGE_ADMIN)) {
                posts = postService.getAllPublishedPostsByPage(page, appSetting.getPageSize());
            } else {
                posts = postService.getAllPublishedNotDeletedPostsByPage(page, appSetting.getPageSize());
            }
        } else {
            posts = postService.getAllPublishedNotCensoredNotDeletedPostsByPage(page, appSetting.getPageSize());
        }

        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("posts", posts);
        model.addAttribute("page", page + 1);

        return "home/index";
    }

    @RequestMapping(value = "about", method = GET)
    public String about(Model model) {

        /*Post post = null;
        try {
            post = postService.getPublishedPostByPermalink(Constants.ABOUT_PAGE_PERMALINK);
        } catch (NotFoundException nfe) {
            logger.debug("Get post with permalink " + Constants.ABOUT_PAGE_PERMALINK);
            post = postService.createAboutPage();
        }

        if (post == null) {
            throw new NotFoundException("Post with permalink " + Constants.ABOUT_PAGE_PERMALINK + " is not found");
        }
        */

        Post post = null;
        try {
            post = postService.getPublishedPostByPermalink(Constants.ABOUT_PAGE_PERMALINK);
        } catch (NotFoundException nfe) {
            logger.debug("Get post with permalink " + Constants.ABOUT_PAGE_PERMALINK);
            throw new NotFoundException("Post with permalink " + Constants.ABOUT_PAGE_PERMALINK + " is not found");
        }

        model.addAttribute("about", post);
        return "home/about";
    }

}
