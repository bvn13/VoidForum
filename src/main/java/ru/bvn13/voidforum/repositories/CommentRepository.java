package ru.bvn13.voidforum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.bvn13.voidforum.models.Comment;
import ru.bvn13.voidforum.models.Post;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostOrderById(Post post);
    List<Comment> findAllByPostAndParentCommentOrderById(Post post, Comment parentComment);

    Page<Comment> findAllByPostAndDeletedMarkOrderById(Post post, Boolean deletedMark, Pageable pageable);
    List<Comment> findAllByPostAndParentCommentAndDeletedMarkOrderById(Post post, Comment parentComment, Boolean deletedMark);

    @Query("SELECT COUNT(c.id) FROM Comment AS c WHERE c.post = :post AND c.deletedMark = :deletedMark")
    Integer getCommentsCountByPostAndDeletedMark(@Param("post") Post post, @Param("deletedMark") Boolean deletedMark);
}
