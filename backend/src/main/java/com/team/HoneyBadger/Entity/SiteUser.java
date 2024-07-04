package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SiteUser {
    // 사용자
    @Id
    @Column(length = 24)
//    @Setter(AccessLevel.NONE)
    private String username;
    private String name;
    private Role role;
    @Column(columnDefinition = "TEXT")
    private String password;
    @Column(length = 11)
    private String phoneNumber;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;

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
