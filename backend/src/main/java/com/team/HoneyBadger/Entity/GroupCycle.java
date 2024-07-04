package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class GroupCycle {
    // 그룹 일정
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private Department group;

    @Builder
    public GroupCycle(String title, String content, LocalDateTime startDate, LocalDateTime endDate, Department group) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.group = group;
    }
}
