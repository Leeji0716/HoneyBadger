package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cycle {
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
    private String k;
    @ManyToOne
    private CycleTag tag;


    @Builder
    public Cycle(String title, String content, String k, LocalDateTime startDate, LocalDateTime endDate,CycleTag tag) {
        this.title = title;
        this.content = content;
        this.k = k;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createDate = LocalDateTime.now();
        this.tag = tag;
    }
}
