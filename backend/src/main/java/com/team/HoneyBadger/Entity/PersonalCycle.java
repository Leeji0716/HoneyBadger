package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PersonalCycle {
    // 개인 일정
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
    private SiteUser user;

    @Builder
    public PersonalCycle(String title, String content, SiteUser user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.startDate = LocalDateTime.now();
        this.endDate = startDate;
    }
}
