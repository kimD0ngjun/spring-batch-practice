package com.example.batchpractice.repository;

import com.example.batchpractice.entity.AfterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AfterRepository extends JpaRepository<AfterEntity, Long> {
}
