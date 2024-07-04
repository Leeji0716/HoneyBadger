package com.team.HoneyBadger.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Alarm {
    // 알림
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(columnDefinition = "LONGTEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String url;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne
    private SiteUser user;

    @Builder
    public Alarm(String message, String url, SiteUser user) {
        this.message = message;
        this.url = url;
        this.user = user;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;

    }
}
