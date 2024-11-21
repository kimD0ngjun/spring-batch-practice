package com.example.batchpractice.repository;

import com.example.batchpractice.entity.BeforeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BeforeJdbcRepository {

    private static final String SQL
            = "SELECT id, username FROM BeforeEntity ORDER BY id LIMIT ? OFFSET ?";

    private final JdbcTemplate jdbcTemplate;

    public List<BeforeEntity> findAll(int pageSize, int offset) {
        return jdbcTemplate.query(SQL,
                (rs, rowNum) -> {
                    BeforeEntity entity = new BeforeEntity();
                    entity.setId(rs.getLong("id"));
                    entity.setUsername(rs.getString("username"));
                    return entity;
                },
                pageSize, offset // 가변 인자 형태로 전달
        );
    }
}
