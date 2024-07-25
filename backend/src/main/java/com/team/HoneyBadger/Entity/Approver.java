package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


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
    private ApprovalStatus approverStatus;
    private LocalDateTime createDate;

    @Builder
    public Approver(SiteUser user, Approval approval, ApprovalStatus approverStatus, LocalDateTime createDate) {
        this.user = user;
        this.approval = approval;
        this.approverStatus = approverStatus;
        this.createDate = createDate;
    }
}
