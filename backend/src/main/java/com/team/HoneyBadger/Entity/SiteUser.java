package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.UserRole;
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
    @Setter(AccessLevel.NONE)
    private String username;
    private String name;
    private UserRole role;
    @Column(columnDefinition = "TEXT")
    private String password;
    @Column(length = 11)
    private String phoneNumber;

    private LocalDateTime joinDate;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
    private boolean active;

    @Builder
    public SiteUser(String username, String name, UserRole role, String password, String phoneNumber, LocalDateTime joinDate, Department department) {
        this.username = username;
        this.name = name;
        this.role = role;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
        this.joinDate = joinDate;
        this.department = department;
        this.active = true;
    }
}
