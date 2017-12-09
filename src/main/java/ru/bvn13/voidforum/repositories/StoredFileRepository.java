package ru.bvn13.voidforum.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.bvn13.voidforum.models.StoredFile;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.bvn13.voidforum.models.User;

@Repository
public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
    StoredFile findById(Long id);
    StoredFile findByName(String name);
    Page<StoredFile> findAllByUserOrderByIdDesc(User user, Pageable pageRequest);
}
