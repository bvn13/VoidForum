package ru.bvn13.voidforum.controllers.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.bvn13.voidforum.forms.PostForm;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.support.*;
import ru.bvn13.voidforum.repositories.PostRepository;
import ru.bvn13.voidforum.repositories.UserRepository;
import ru.bvn13.voidforum.services.PostService;
import ru.bvn13.voidforum.services.PrivilegeService;
import ru.bvn13.voidforum.services.UserService;
import ru.bvn13.voidforum.utils.DTOUtil;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created by bvn13 on 07.12.2017.
 */
@Controller("accountPostController")
@RequestMapping(value = "/account/posts")
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

    @GetMapping(name = "")
    public String getOwnedPosts(@RequestParam(defaultValue = "0") int page, Model model) {

        Assert.notNull(userService.currentUser(), "");

        Page<Post> posts = postRepository.findAllByUser(userService.currentUser(), new PageRequest(page, PAGE_SIZE, Sort.Direction.DESC, "id"));

        model.addAttribute("totalPages", posts.getTotalPages());
        model.addAttribute("page", page);
        model.addAttribute("posts", posts);

        return "account/posts/index";
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

        return "account/posts/new";
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

        return "account/posts/edit";
    }

    private void checkAccess(Long postId) {
        if (!postService.getPost(postId).getUser().getId().equals(userService.currentUser().getId())) {
            throw new AccessDeniedException("You are not allowed to be here");
        }
    }

    @RequestMapping(value = "{postId:[0-9]+}/edit")
    public String editPost(@PathVariable Long postId, Model model) {
        this.checkAccess(postId);
        return this.makeFormPostEdition(postId, model);
    }


    @RequestMapping(value = "new")
    public String newPost(Model model){
        return this.makeFormPostCreation(model);
    }


    @RequestMapping(value = "{postId:[0-9]+}/delete", method = {DELETE, POST})
    public String deletePost(@PathVariable Long postId){
        this.checkAccess(postId);
        postService.deletePost(postRepository.findOne(postId));
        return "redirect:/account/posts";
    }

    @RequestMapping(value = "", method = POST)
    public String create(Principal principal, @Valid PostForm postForm, Errors errors, Model model){
        if (errors.hasErrors()) {
            Map<String, WebError> webErrors = new HashMap<>();
            errors.getAllErrors().forEach(e -> {
                String field = ((FieldError)e).getField();
                webErrors.put(field, new WebError(field, e.getDefaultMessage()));
            });
            model.addAttribute("errors", webErrors);
            return this.makeFormPostCreation(model, postForm);
        } else {
            Post post = DTOUtil.map(postForm, Post.class);
            post.setUser(userRepository.findByEmail(principal.getName()));
            post.setTags(postService.parseTagNames(postForm.getPostTags()));
            postForm.fillOgFieldsInPost(post);

            postService.createPost(post);

            return "redirect:/account/posts";
        }
    }

    @RequestMapping(value = "{postId:[0-9]+}", method = {PUT, POST})
    public String update(@PathVariable Long postId, @Valid PostForm postForm, Errors errors, Model model){
        this.checkAccess(postId);
        if (errors.hasErrors()){
            Map<String, WebError> webErrors = new HashMap<>();
            errors.getAllErrors().forEach(e -> {
                String field = ((FieldError)e).getField();
                webErrors.put(field, new WebError(field, e.getDefaultMessage()));
            });
            model.addAttribute("errors", webErrors);
            return this.makeFormPostEdition(postId, model, postForm);
        } else {
            Post post = postRepository.findOne(postId);
            DTOUtil.mapTo(postForm, post);
            post.setTags(postService.parseTagNames(postForm.getPostTags()));
            postForm.fillOgFieldsInPost(post);

            postService.updatePost(post);

            return "redirect:/account/posts";
        }
    }

}
