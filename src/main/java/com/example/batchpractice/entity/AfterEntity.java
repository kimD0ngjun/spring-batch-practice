package com.example.batchpractice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 옮기려는 데이터 도착 테이블
 */
@Entity(name = "AfterEntity")
@Getter
@Setter
@NoArgsConstructor
public class AfterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    public AfterEntity(String username) {
        this.username = username;
    }
}
