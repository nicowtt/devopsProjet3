package com.openclassrooms.dataShare.repository;

import com.openclassrooms.dataShare.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
}