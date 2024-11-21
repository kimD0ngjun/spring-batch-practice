package com.example.batchpractice.repository.jpa;

import com.example.batchpractice.entity.BeforeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeforeJpaRepository extends JpaRepository<BeforeEntity, Long> {

//    Optional<BeforeEntity> findById(Long id);
}
