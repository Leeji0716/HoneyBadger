package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.ApprovalStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    private String title;
    private ApprovalStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser approver;
    @OneToMany(mappedBy = "approval", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Viewer> viewers;

    @Builder
    public Approval(String title, ApprovalStatus status, SiteUser sender, SiteUser approver, List<Viewer> viewers) {
        this.title = title;
        this.status = status;
        this.sender = sender;
        this.approver = approver;
        this.viewers = viewers;
    }
}
