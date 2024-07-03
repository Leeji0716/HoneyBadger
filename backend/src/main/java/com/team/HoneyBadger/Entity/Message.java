package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.MessageType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;

@Entity
@Getter
@Setter
public class Message {
    // 메시지
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    private Chatroom chatroom;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender;

    private HashSet<String> reads;
    private MessageType messageType;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @Builder
    public Message(String message, Chatroom chatroom, SiteUser sender, HashSet<String> reads, MessageType messageType) {
        this.message = message;
        this.chatroom = chatroom;
        this.sender = sender;
        this.reads = reads;
        this.messageType = messageType;
        this.createDate = LocalDateTime.now();
        this.modifyDate = LocalDateTime.now();
    }
}
