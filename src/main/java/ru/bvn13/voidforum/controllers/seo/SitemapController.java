package ru.bvn13.voidforum.controllers.seo;

import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.services.PostService;
import ru.bvn13.voidforum.services.SeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = {"/seo", ""})
public class SitemapController {

    @Autowired
    private PostService postService;

    @Autowired
    private SeoService seoService;

    @GetMapping(value = "/sitemap", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    String getSiteMap() {
        List<Post> posts = this.postService.getAllPublishedPosts();
        return this.seoService.createSitemap(posts);
    }

}
