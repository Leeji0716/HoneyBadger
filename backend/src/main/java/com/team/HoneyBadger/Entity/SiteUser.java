package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class SiteUser {
    // 사용자
    @Id
    @Column(length = 24)
    private String username;
    private String name;
    private Role role;
    private String password;
    private String phoneNumber;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Auth auth;

    @ManyToOne(fetch = FetchType.LAZY)
    private Group group;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @Builder
    public SiteUser(String username, String name, Role role, String password, String phoneNumber) {
        this.username = username;
        this.name = name;
        this.role = role;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
    }
}
