package ru.bvn13.voidforum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bvn13.voidforum.models.Comment;
import ru.bvn13.voidforum.models.Post;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostOrderById(Post post);
    List<Comment> findAllByPostAndParentCommentOrderById(Post post, Comment parentComment);

    List<Comment> findAllByPostAndDeletedMarkOrderById(Post post, Boolean deletedMark);
    List<Comment> findAllByPostAndParentCommentAndDeletedMarkOrderById(Post post, Comment parentComment, Boolean deletedMark);

}
