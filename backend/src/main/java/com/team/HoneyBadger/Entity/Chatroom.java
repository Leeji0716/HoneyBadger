package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class Chatroom {
    // 채팅방
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @OneToMany(mappedBy = "chatroom", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Participant> participants;

    @ManyToOne(fetch = FetchType.LAZY)
    private Message notification;

    @Builder
    public Chatroom(String name) {
        this.name = name;
        this.participants = new ArrayList<>();
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
    }
}
