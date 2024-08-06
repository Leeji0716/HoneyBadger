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
public class EmailReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    @ManyToOne(fetch = FetchType.EAGER)
    private SiteUser sender;

    private List<String> receiverList;

    private LocalDateTime sendTime;

    private LocalDateTime createTime;

    @Builder
    public EmailReservation(String title, String content, SiteUser sender, LocalDateTime sendTime, List<String> receiverList) {
        this.title = title;
        this.content = content;
        this.sender = sender;
        this.sendTime = sendTime;
        this.receiverList = new ArrayList<>();
        this.createTime = LocalDateTime.now();
        this.receiverList = receiverList;
    }
}