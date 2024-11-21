package com.example.batchpractice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 옮기려는 데이터 출발 테이블
 */
@Entity(name = "BeforeEntity")
@Getter
@Setter
public class BeforeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
}
