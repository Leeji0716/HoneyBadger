package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(length = 100)
    private String title;
    private boolean duplicate;
    private LocalDateTime endDate;
    private LocalDateTime startDate;
    private LocalDateTime modifyDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser creator;
    @OneToMany(mappedBy = "vote", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<VoteContent> contents;

    @Builder
    public Vote(String title, boolean duplicate, LocalDateTime endDate, LocalDateTime startDate, LocalDateTime modifyDate, SiteUser creator) {
        this.title = title;
        this.duplicate = duplicate;
        this.endDate = endDate;
        this.startDate = startDate;
        this.modifyDate = modifyDate;
        this.creator = creator;
    }
}
