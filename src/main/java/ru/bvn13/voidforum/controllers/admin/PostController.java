package ru.bvn13.voidforum.controllers.admin;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.bvn13.voidforum.forms.PostForm;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.support.*;
import ru.bvn13.voidforum.repositories.PostRepository;
import ru.bvn13.voidforum.repositories.UserRepository;
import ru.bvn13.voidforum.services.CommentService;
import ru.bvn13.voidforum.services.PostService;
import ru.bvn13.voidforum.services.PrivilegeService;
import ru.bvn13.voidforum.services.UserService;
import ru.bvn13.voidforum.support.web.MessageHelper;
import ru.bvn13.voidforum.utils.DTOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * bvn13 <mail4bvn@gmail.com>
 */
@Controller("adminPostController")
@RequestMapping("admin/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;



    private static final int PAGE_SIZE = 20;

    @RequestMapping(value = "")
    public String index(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Post> posts = postRepository.findAll(new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "id"));

        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("posts", posts);

        return "admin/posts/index";
    }

    private String makeFormPostCreation(Model model) {
        PostForm postForm = DTOUtil.map(new Post(), PostForm.class);
        postForm.init();
        return this.makeFormPostCreation(model, postForm);
    }

    private String makeFormPostCreation(Model model, PostForm postForm) {

        User user = userService.currentUser();
        List<PostFormat> availableFormats = userService.getAvailablePostFormats(user);

        model.addAttribute("postForm", postForm);
        model.addAttribute("postFormats", availableFormats);
        model.addAttribute("postStatus", PostStatus.values());
        model.addAttribute("seoOgLocales", OgLocale.values());
        model.addAttribute("seoOgTypes", OgType.values());

        return "admin/posts/new";
    }

    @RequestMapping(value = "new")
    public String newPost(Model model){
        return this.makeFormPostCreation(model);
    }

    private String makeFormPostEdition(Long postId, Model model) {
        return this.makeFormPostEdition(postId, model, null);
    }

    private String makeFormPostEdition(Long postId, Model model, PostForm postForm) {
        Post post = postRepository.findOne(postId);

        if (postForm == null) {
            postForm = DTOUtil.map(post, PostForm.class);
        }

        postForm.init();
        DTOUtil.mapTo(post, postForm);
        postForm.initFromPost(post, postService.getTagNames(post.getTags()));

        User user = userService.currentUser();
        List<PostFormat> availableFormats = userService.getAvailablePostFormats(user);

        model.addAttribute("post", post);
        model.addAttribute("postForm", postForm);
        model.addAttribute("postFormats", availableFormats);
        model.addAttribute("postStatus", PostStatus.values());
        model.addAttribute("seoOgLocales", OgLocale.values());
        model.addAttribute("seoOgTypes", OgType.values());

        return "admin/posts/edit";
    }

    @RequestMapping(value = "{postId:[0-9]+}/edit")
    public String editPost(@PathVariable Long postId, Model model){
        return this.makeFormPostEdition(postId, model);
    }

    @RequestMapping(value = "{postId:[0-9]+}/delete", method = {DELETE, POST})
    public String deletePost(@PathVariable Long postId){
        postService.deletePost(postRepository.findOne(postId));
        return "redirect:/admin/posts";
    }

    @RequestMapping(value = "{postId:[0-9]+}/censore", method = {PUT, POST})
    public String censorePost(@PathVariable Long postId){
        postService.censorePost(postRepository.findOne(postId));
        return "redirect:/admin/posts";
    }



    @RequestMapping(value = "", method = POST)
    public String create(Principal principal, @Valid PostForm postForm, Errors errors, Model model, RedirectAttributes ra){
        if (errors.hasErrors()) {
            MessageHelper.addNamedErrorsAsList(ra, "errors", "Please check following errors:", errors);
            return this.makeFormPostCreation(model, postForm);
        } else {
            Post post = DTOUtil.map(postForm, Post.class);
            post.setUser(userRepository.findByEmail(principal.getName()));
            post.setTags(postService.parseTagNames(postForm.getPostTags()));
            postForm.fillOgFieldsInPost(post);

            postService.createPost(post);

            return "redirect:/admin/posts";
        }
    }

    @RequestMapping(value = "{postId:[0-9]+}", method = {PUT, POST})
    public String update(@PathVariable Long postId, @Valid PostForm postForm, Errors errors, Model model, RedirectAttributes ra){
        if (errors.hasErrors()) {
            MessageHelper.addNamedErrorsAsList(ra, "errors", "Please check following errors:", errors);
            return this.makeFormPostEdition(postId, model, postForm);
        } else {
            Post post = postRepository.findOne(postId);
            DTOUtil.mapTo(postForm, post);
            post.setTags(postService.parseTagNames(postForm.getPostTags()));
            postForm.fillOgFieldsInPost(post);

            postService.updatePost(post);

            return "redirect:/admin/posts";
        }
    }




}
