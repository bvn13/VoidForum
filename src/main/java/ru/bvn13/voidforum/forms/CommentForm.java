package ru.bvn13.voidforum.forms;

import lombok.Data;
import ru.bvn13.voidforum.models.Comment;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.support.CommentFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Created by bvn13 on 08.12.2017.
 */
@Data
public class CommentForm {

    @NotNull
    private Integer postId;

    private Integer parentCommentId;

    @NotEmpty
    private String content;

    @NotNull
    private CommentFormat commentFormat;

}
