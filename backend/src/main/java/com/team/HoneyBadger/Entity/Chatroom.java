package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @OneToMany( mappedBy = "chatroom", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Participant> participants;
    @OneToMany(mappedBy = "chatroom", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Message> messageList;

    @Builder
    public Chatroom(String name) {
        this.name = name;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
        this.participants = new ArrayList<>();
        this.messageList = new ArrayList<>();

    }
}
