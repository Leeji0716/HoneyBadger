package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EmailTag {
    // 이메일 태그
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser user;

    @Builder
    public EmailTag(String name, SiteUser user) {
        this.name = name;
        this.user = user;
    }
}
