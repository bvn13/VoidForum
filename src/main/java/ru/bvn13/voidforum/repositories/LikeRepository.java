package ru.bvn13.voidforum.repositories;

import ru.bvn13.voidforum.models.Like;
import ru.bvn13.voidforum.models.Post;
import ru.bvn13.voidforum.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT SUM(l.sympathy) FROM Like AS l WHERE l.user = :user AND l.post = :post GROUP BY l.user, l.post")
    Integer getTotalLikesByUserAndPost(@Param("user") User user, @Param("post") Post post);

    @Query("SELECT SUM(l.sympathy) FROM Like AS l WHERE l.post = :post GROUP BY l.post")
    Integer getTotalLikesByPost(@Param("post") Post post);

    @Query("SELECT SUM(l.sympathy) FROM Like AS l WHERE l.clientIp = :clientIp AND l.post = :post GROUP BY l.clientIp, l.post")
    Integer getTotalLikesByClientIpAndPost(@Param("clientIp") String clientIp, @Param("post") Post post);
}
