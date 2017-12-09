package ru.bvn13.voidforum.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "privileges")
@Getter
@Setter
public class Privilege extends BaseModel {

    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "privileges")
    private Collection<Role> roles;

    public Privilege() {}

    public Privilege(String name) {
        this.name = name;
    }

}
