package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String title;
    private ApprovalStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender; //보낸사람
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser approver; //승인자
    @OneToMany(mappedBy = "approval", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Viewer> viewers; //참고인

    @Builder
    public Approval(String title, ApprovalStatus status, SiteUser sender, SiteUser approver, List<Viewer> viewers) {
        this.title = title;
        this.status = status;
        this.sender = sender;
        this.approver = approver;
        this.viewers = viewers;
    }
}
