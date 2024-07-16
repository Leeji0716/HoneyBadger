package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Email {
    // 이메일
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    @ManyToOne(fetch = FetchType.EAGER)
    private SiteUser sender;
    @OneToMany(mappedBy = "email", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EmailReceiver> receiverList;

    private LocalDateTime createDate;

    @ElementCollection
    private List<String> recipients = new ArrayList<>();

    @ElementCollection
    private Map<String, Boolean> readStatus = new HashMap<>();  // 읽은 사용자 목록 저장

    @Builder
    public Email(String title, String content, SiteUser sender) {
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.createDate = LocalDateTime.now();
//        this.tag = tag;
        this.receiverList = new ArrayList<>();
    }

    public void markAsRead(String username) {
        readStatus.put(username, true);
    }

    public boolean isReadByUser(String username) {
        return readStatus.getOrDefault(username, false);
    }
}
