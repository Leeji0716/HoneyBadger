package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDateTime createDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser user;

    private List<String> tag;

    @Builder
    public PersonalCycle(String title, String content, SiteUser user, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createDate = LocalDateTime.now();
    }
}
