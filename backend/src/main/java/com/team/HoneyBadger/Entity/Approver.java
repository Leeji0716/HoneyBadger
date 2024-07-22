package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Approver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Approval approval;


    @Builder
    public Approver(SiteUser user, Approval approval) {
        this.user = user;
        this.approval = approval;
    }
}
