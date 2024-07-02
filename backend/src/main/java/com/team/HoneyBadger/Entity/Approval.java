package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Approval {
    // 결재
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser approver;
    private String title;
    private String content;
    private ApprovalStatus status;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public Approval(SiteUser sender, SiteUser approver, String title, String content) {
        this.sender = sender;
        this.approver = approver;
        this.title = title;
        this.content = content;
        this.status = ApprovalStatus.UNREAD;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
    }
}
