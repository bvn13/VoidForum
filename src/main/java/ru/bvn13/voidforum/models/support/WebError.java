package ru.bvn13.voidforum.models.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class WebError implements Serializable {

    private String field;

    private String errorMessage;

    public WebError(String field, String errorMessage) {
        this.field = field;
        this.errorMessage = errorMessage;
    }

}
