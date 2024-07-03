package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Email {
    // 이메일
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender;
    private String title;
    private String content;

    @Builder
    public Email(SiteUser sender, String title, String content) {
        this.sender = sender;
        this.title = title;
        this.content = content;
    }
}
