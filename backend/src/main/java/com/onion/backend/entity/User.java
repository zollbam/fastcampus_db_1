package com.onion.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @Column(columnDefinition = "json")
    @Convert(converter = DeviceListConverter.class)
    private List<Device> deviceList = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, insertable = true)
    private LocalDateTime createdDate; // 생성일 (가입일)

    @LastModifiedDate
    private LocalDateTime updatedDate; // 갱신일

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        if (deviceList == null) {
            deviceList = new ArrayList<>(); // 기본값을 빈 배열로 설정
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return false;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
}

