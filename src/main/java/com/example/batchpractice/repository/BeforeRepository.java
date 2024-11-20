package com.example.batchpractice.repository;

import com.example.batchpractice.entity.BeforeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeforeRepository extends JpaRepository<BeforeEntity, Long> {
}
