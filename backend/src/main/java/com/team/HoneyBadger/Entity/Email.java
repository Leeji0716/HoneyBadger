package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private Boolean isScheduled;  // 예약된 이메일을 나타내는 필드 추가

    @Builder
    public Email(String title, String content, SiteUser sender) {
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.createDate = LocalDateTime.now();
//        this.tag = tag;
        this.receiverList = new ArrayList<>();
    }
}
