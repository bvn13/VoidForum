package ru.bvn13.voidforum.services;

import com.domingosuarez.boot.autoconfigure.jade4j.JadeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.bvn13.voidforum.models.Comment;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.User;
import ru.bvn13.voidforum.models.support.CommentFormat;
import ru.bvn13.voidforum.repositories.CommentRepository;
import ru.bvn13.voidforum.support.web.MarkdownService;

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

    @Autowired
    private MarkdownService markdownService;


    public Comment getCommentById(Long commentId) {
        return commentRepository.findOne(commentId);
    }

    private static Comparator<Comment> commentComparator = new Comparator<Comment>() {
        @Override
        public int compare(Comment o1, Comment o2) {
            return o1.getId().compareTo(o2.getId());
        }
    };


    public Page<Comment> getCommentsForPost(Post post, int page, int pageSize) {
        Page<Comment> availableComments = commentRepository.findAllByPostAndDeletedMarkOrderById(post, false, new PageRequest(page, pageSize, Sort.Direction.ASC, "createdAt"));
        return availableComments;
    }
    public Page<Comment> getCommentsForPostForAdmin(Post post, int page, int pageSize) {
        Page<Comment> availableComments = commentRepository.findAllByPostOrderById(post, new PageRequest(page, pageSize, Sort.Direction.ASC, "createdAt"));
        return availableComments;
    }

    public Integer getLastPageCommentsForPost(Post post, int pageSize) {
        Integer count = commentRepository.getCommentsCountByPostAndDeletedMark(post, false);
        return (int) Math.ceil(count.intValue() / pageSize);
    }

    public Integer getLastPageCommentsForPostForAdmin(Post post, int pageSize) {
        Integer count = commentRepository.getCommentsCountByPost(post);
        return (int) Math.ceil(count.intValue() / pageSize);
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

    public Integer calculateDepth(Comment comment) {
        if (comment.getParentComment() == null) {
            return 0;
        }
        return 1 + calculateDepth(comment.getParentComment());
    }

    public Comment saveComment(Comment comment) {
        if (comment.getCommentFormat() == CommentFormat.MARKDOWN) {
            comment.setRenderedContent(String.format("<div class=\"markdown-post\">%s</div>", markdownService.renderToHtml(comment.getContent())));
        } else {
            comment.setRenderedContent(String.format("<div class=\"html-post\">%s</div>", comment.getContent()));
        }
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = this.getCommentById(commentId);
        comment.setDeletedMark(!comment.getDeletedMark());
        commentRepository.save(comment);
    }

}
