package ru.bvn13.voidforum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bvn13.voidforum.models.Role;
import ru.bvn13.voidforum.models.User;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String role_name);
    @Query("SELECT r FROM Role AS r " +
            "INNER JOIN r.users AS u " +
            "WHERE u = :user")
    List<Role> findAllByUser(@Param("user") User user);
}
