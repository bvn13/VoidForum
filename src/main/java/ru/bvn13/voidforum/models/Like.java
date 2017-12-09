package ru.bvn13.voidforum.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "posts_likes")
@Getter
@Setter
public class Like extends BaseModel {

    @ManyToOne
    private User user;

    @ManyToOne
    private Post post;

    @Column(nullable = false)
    private Integer sympathy;

    @Column(nullable = false)
    private String clientIp;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isAdmin;

}
