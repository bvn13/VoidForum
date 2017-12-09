package ru.bvn13.voidforum.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bvn13.voidforum.models.Tag;

/**
 * bvn13 <mail4bvn@gmail.com>.
 */
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String name);
}
