package ru.bvn13.voidforum.models;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import ru.bvn13.voidforum.models.support.CommentFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment extends BaseModel {

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Comment parentComment;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentComment", cascade = CascadeType.REMOVE)
    private Collection<Comment> children = new ArrayList<>();


    @Type(type="text")
    @Getter
    @Setter
    private String content;

    @Type(type = "text")
    private String renderedContent;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentFormat commentFormat = CommentFormat.MARKDOWN;

    @Column(nullable = false, columnDefinition = "boolean DEFAULT false")
    private Boolean deletedMark;

    @Column(nullable = false, columnDefinition = "integer DEFAULT 0")
    private Integer depth;
}
