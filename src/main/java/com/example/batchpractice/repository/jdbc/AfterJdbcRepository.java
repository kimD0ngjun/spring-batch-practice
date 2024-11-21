package com.example.batchpractice.repository.jdbc;

import com.example.batchpractice.entity.AfterEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AfterJdbcRepository {

    private static final String SQL
            = "INSERT INTO AfterEntity (id, username) VALUES (?, ?)";

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void save(AfterEntity afterEntity) {
        jdbcTemplate.update(SQL, afterEntity.getId(), afterEntity.getUsername());
    }

    @Transactional
    public void batchSave(List<AfterEntity> afterEntities) {
        jdbcTemplate.batchUpdate(SQL, afterEntities, afterEntities.size(),
                (ps, afterEntity) -> {
                    ps.setLong(1, afterEntity.getId());
                    ps.setString(2, afterEntity.getUsername());
                });
    }

}
