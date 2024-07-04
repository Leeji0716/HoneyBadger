package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Participant {
    // 채팅 참가자
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private SiteUser user;
    @ManyToOne(fetch = FetchType.EAGER)
    private Chatroom chatroom;

    @Builder
    public Participant(SiteUser user, Chatroom chatroom) {
        this.user = user;
        this.chatroom = chatroom;
    }
}
