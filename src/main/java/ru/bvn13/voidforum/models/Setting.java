package ru.bvn13.voidforum.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * A generic setting model
 *
 * bvn13 <mail4bvn@gmail.com>
 */
@Entity
@Table(name = "settings")
@Getter
@Setter
public class Setting extends BaseModel{

    @Column(name = "_key", unique = true, nullable = false)
    private String key;

    @Lob
    @Column(name = "_value")
    private Serializable value;

}
