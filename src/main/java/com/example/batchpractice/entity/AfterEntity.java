package com.example.batchpractice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 옮기려는 데이터 도착 테이블
 */
@Entity
@Getter
@Setter
public class AfterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
}
