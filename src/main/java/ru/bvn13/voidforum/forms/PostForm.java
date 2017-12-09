package ru.bvn13.voidforum.forms;

import lombok.Data;
import javax.validation.constraints.NotEmpty;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.SeoPostData;
import ru.bvn13.voidforum.models.support.OgLocale;
import ru.bvn13.voidforum.models.support.OgType;
import ru.bvn13.voidforum.models.support.PostFormat;
import ru.bvn13.voidforum.models.support.PostStatus;

import javax.validation.constraints.NotNull;

/**
 * bvn13 <mail4bvn@gmail.com>
 */
@Data
public class PostForm {
    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    @NotNull
    private PostFormat postFormat;

    @NotNull
    private PostStatus postStatus;

    @NotNull
    private String permalink;

    @NotNull
    private String postTags;

    @NotNull
    private String seoKeywords;

    @NotNull
    private String seoDescription;


//    @NotNull
//    private String seoOgTitle;

    @NotNull
    private OgType seoOgType;

    @NotNull
    private String seoOgImage;

    @NotNull
    private String seoOgVideo;

    @NotNull
    private OgLocale seoOgLocale;

    @NotNull
    private Boolean deletedMark;

    @NotNull
    private Boolean censored;

    public void init() {
        this.setTitle("");
        this.setPermalink("");
        this.setContent("");
        this.setPostTags("");
        this.setPostStatus(PostStatus.DRAFT);
        this.setPostFormat(PostFormat.MARKDOWN);
        this.setSeoKeywords("");
        this.setSeoOgImage("");
        this.setSeoOgLocale(OgLocale.ru_RU);
        //this.setSeoOgTitle("");
        this.setSeoOgType(OgType.WEBSITE);
        this.setSeoOgVideo("");
        this.setDeletedMark(false);
        this.setCensored(false);
    }

    public void initFromPost(Post post) {
        if (post.getSeoData() != null) {
            this.setSeoOgImage(post.getSeoData().getOgImage());
            this.setSeoOgVideo(post.getSeoData().getOgVideo());
            this.setSeoOgLocale(post.getSeoData().getOgLocale());
            this.setSeoOgType(post.getSeoData().getOgType());
        } else {
            this.setSeoOgImage("");
            this.setSeoOgLocale(OgLocale.ru_RU);
            //this.setSeoOgTitle("");
            this.setSeoOgType(OgType.WEBSITE);
            this.setSeoOgVideo("");
        }
    }

    public void initFromPost(Post post, String postTags) {
        this.initFromPost(post);
        this.setPostTags(postTags);
    }

    public void fillOgFieldsInPost(Post post) {
        SeoPostData data = null;
        if (post.getSeoData() == null) {
            data = new SeoPostData();
        } else {
            data = post.getSeoData();
        }
        data.setOgImage(this.seoOgImage);
        data.setOgLocale(this.seoOgLocale);
        data.setOgTitle(this.title);
        data.setOgType(this.seoOgType);
        data.setOgVideo(this.seoOgVideo);
        post.setSeoData(data);
    }

}
