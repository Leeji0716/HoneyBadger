package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.AttendanceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Attendance {
    // 출석 관리
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private LocalDateTime login;
    private LocalDateTime logout;
    private AttendanceType status;
    @ManyToOne
    private SiteUser user;
}
