package ru.bvn13.voidforum.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import ru.bvn13.voidforum.models.support.AppLocale;
import ru.bvn13.voidforum.services.RoleService;
import ru.bvn13.voidforum.services.UserService;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

/**
 * bvn13 <mail4bvn@gmail.com>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseModel {

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    @JsonIgnore
    private String password;

    private Boolean disabled;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "users_roles",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Collection<Role> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private Collection<Post> posts = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private Collection<StoredFile> storedFiles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private Collection<Comment> comments = new ArrayList<>();

    @Column
    private AppLocale locale;

    public User() {

    }

    /*
    public Boolean isAdmin() {
        return UserService.hasRole(this, RoleService.ROLE_ADMIN) || UserService.hasRole(this, RoleService.ROLE_OWNER);
    }

    public Boolean isOwner() {
        return UserService.hasRole(this, RoleService.ROLE_OWNER);
    }
    */

    public User(String email, String nickname, String password, String role) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.roles.add(new Role(role));
    }
}
