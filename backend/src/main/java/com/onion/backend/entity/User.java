package com.onion.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "`user`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(nullable = false)
    private String username; // 사용자명

    @JsonIgnore
    @Column(nullable = false)
    private String password; // 비밀번호

    @JsonIgnore
    @Column(nullable = false)
    private String email; // 이메일

    private LocalDateTime lastLoginAt; // 최근 로그인 시간

    @CreatedDate
    @Column(nullable = false, insertable = true)
    private LocalDateTime createdAt; // 생성일 (가입일)

    @LastModifiedDate
    private LocalDateTime updatedAt; // 갱신일
}

