package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.ApproverStatus;
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
    private ApproverStatus approverStatus;


    @Builder
    public Approver(SiteUser user, Approval approval, ApproverStatus approverStatus) {
        this.user = user;
        this.approval = approval;
        this.approverStatus = approverStatus;
    }
}
