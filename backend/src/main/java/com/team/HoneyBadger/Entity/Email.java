package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Email {
    // 이메일
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender;
    @OneToMany(mappedBy = "email", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EmailReceiver> receiverList;
    @ManyToOne(fetch = FetchType.LAZY)
    private EmailTag tag;

    @Builder
    public Email(String title, String content, SiteUser sender,List<EmailReceiver> receivers) {
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.receiverList = receivers;
    }
}
