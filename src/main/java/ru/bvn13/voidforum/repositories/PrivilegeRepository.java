package ru.bvn13.voidforum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.bvn13.voidforum.models.Privilege;
import ru.bvn13.voidforum.models.Role;

import java.util.Collection;
import java.util.List;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByName(String name);
    @Query("SELECT p FROM Privilege AS p " +
            "INNER JOIN p.roles AS r " +
            "WHERE r = :role")
    List<Privilege> findAllByRole(@Param("role") Role role);
    /*@Query("SELECT p FROM Privilege AS p " +
            "INNER JOIN p.roles AS r " +
            "WHERE r IN :roles")
    List<Privilege> findAllByRolesList(@Param("roles") Collection<Role> roles);*/
}
