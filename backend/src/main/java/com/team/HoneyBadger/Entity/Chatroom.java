package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter

public class Chatroom {
    // 채팅방
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)

    private Long id;
    private String name;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @OneToOne(fetch = FetchType.LAZY)
    private Message notification;

    @OneToMany(mappedBy = "chatroom", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Participant> participants;
    @OneToMany(mappedBy = "chatroom", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Message> messageList;

    @Builder
    public Chatroom(String name, LocalDateTime createDate, LocalDateTime modifyDate,List<Participant> participants) {
        this.name = name;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
        this.participants = participants;
        this.messageList = new ArrayList<>();

    }
}
