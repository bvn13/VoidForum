package ru.bvn13.voidforum.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "visits")
@Getter
@Setter
public class Visit extends BaseModel {

    @ManyToOne
    private User user;

    @Column(nullable = false)
    private String clientIp;

    @ManyToOne
    private Post post;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isAdmin;

    @Column
    private String userAgent;

}
