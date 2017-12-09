package ru.bvn13.voidforum.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bvn13.voidforum.models.Role;
import ru.bvn13.voidforum.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * bvn13 <mail4bvn@gmail.com>
 */
@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByNickname(String nickname);
    @Query("SELECT u FROM User AS u " +
            "INNER JOIN u.roles AS r " +
            "WHERE r.name = :roleName")
    List<User> findAllByOneRoleByNameOrderById(@Param("roleName") String rname);
    @Query("SELECT u FROM User AS u " +
            "INNER JOIN u.roles AS r " +
            "WHERE r = :role")
    List<User> findAllByOneRoleOrderById(@Param("role") Role role);
}
