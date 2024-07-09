package com.team.HoneyBadger.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @JsonBackReference
    private SiteUser user;
    @ManyToOne(fetch = FetchType.EAGER)
    private Chatroom chatroom;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @Builder
    public Participant(SiteUser user, Chatroom chatroom) {
        this.user = user;
        this.chatroom = chatroom;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
    }
}
