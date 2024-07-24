package com.team.HoneyBadger.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.team.HoneyBadger.Enum.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    private ApprovalStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender; //보낸사람
    @OneToMany( mappedBy = "approval", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Approver> approvers; //승인자
    @OneToMany(mappedBy = "approval", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Viewer> viewers; //참고인
    private List<String> readUsers;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @Builder
    public Approval(String title, String content, ApprovalStatus status, SiteUser sender) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.sender = sender;
        this.approvers = new ArrayList<> ();
        this.viewers = new ArrayList<> ();
        this.readUsers = new ArrayList<> ();
        this.createDate = LocalDateTime.now ();
        this.modifyDate = createDate;
    }
}
