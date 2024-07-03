package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.RecentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Recent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser user;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser target;
    private RecentType type;

    @Builder
    public Recent(SiteUser user, SiteUser target, RecentType type) {
        this.user = user;
        this.target = target;
        this.type = type;
    }
}
