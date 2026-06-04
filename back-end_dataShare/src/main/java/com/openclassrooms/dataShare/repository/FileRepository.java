package com.openclassrooms.dataShare.repository;

import com.openclassrooms.dataShare.entities.File;
import com.openclassrooms.dataShare.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByUuid(UUID uuid);

    List<File> findAllByOwner(User user);
}