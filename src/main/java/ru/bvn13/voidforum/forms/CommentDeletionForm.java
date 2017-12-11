package ru.bvn13.voidforum.forms;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by bvn13 on 11.12.2017.
 */
@Data
public class CommentDeletionForm {

    @NotNull
    private Long postId;

}
