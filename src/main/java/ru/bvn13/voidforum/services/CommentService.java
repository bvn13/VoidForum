package ru.bvn13.voidforum.services;

import com.domingosuarez.boot.autoconfigure.jade4j.JadeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.bvn13.voidforum.models.Comment;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.support.CommentFormat;
import ru.bvn13.voidforum.repositories.CommentRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by bvn13 on 09.12.2017.
 */
@JadeHelper("commentService")
//@Service
public class CommentService {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentRepository commentRepository;


    private static Comparator<Comment> commentComparator = new Comparator<Comment>() {
        @Override
        public int compare(Comment o1, Comment o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };


    public List<Comment> getCommentsForPost(Post post) {
        List<Comment> availableComments = null;
        //User user = userService.currentUser();
        //if (userService.hasPrivilege(user, PrivilegeService.PRIVILEGE_WRITE)) {
            availableComments = commentRepository.findAllByPostOrderById(post);
        //} else {
        //    availableComments = commentRepository.findAllByPostAndDeletedMarkOrderById(post, false);
        //}
        return availableComments;
    }

    public List<Comment> filterListByParentComment(List<Comment> comments, Comment parent) {
        List<Comment> children = new ArrayList<>();
        comments.forEach(c -> {
            if (c.getParentComment().getId().equals(parent.getId())) {
                children.add(c);
            }
        });
        Collections.sort(children, CommentService.commentComparator);
        return children;
    }

    public Integer childrenCount(List<Comment> comments, Comment parent) {
        AtomicReference<Integer> count = new AtomicReference<>();
        comments.forEach(c -> {
            if (c.getParentComment().getId().equals(parent.getId())) {
                Integer old = count.get();
                count.set(old+1);
            }
        });
        return count.get();
    }

    public List<CommentFormat> getAvailableCommentFormats() {
        List<CommentFormat> formats = new ArrayList<>();
        User user = userService.currentUser();
        for (CommentFormat f : CommentFormat.values()) {
            if (f.equals(CommentFormat.HTML)) {
                if (userService.hasPrivilege(user, PrivilegeService.PRIVILEGE_WRITE_HTML)) {
                    formats.add(f);
                }
            } else {
                formats.add(f);
            }
        }
        return formats;
    }

}
