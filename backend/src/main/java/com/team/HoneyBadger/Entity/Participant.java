package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Participant {
    // 채팅방 참가자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Chatroom chatroom;

    private LocalDateTime createDate;

    public Participant(SiteUser user, Chatroom chatroom) {
        this.user = user;
        this.chatroom = chatroom;
        this.createDate = LocalDateTime.now();
    }
}
