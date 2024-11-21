package com.example.batchpractice.repository;

import com.example.batchpractice.entity.BeforeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeforeJpaRepository extends JpaRepository<BeforeEntity, Long> {

//    Optional<BeforeEntity> findById(Long id);
}
