package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.MessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class Message {
    // 채팅 메시지
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(columnDefinition = "LONGTEXT")
    private String message;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private MessageType messageType;
    private Set<String> reads;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender;
    @ManyToOne(fetch = FetchType.LAZY)
    private Chatroom chatroom;

    @Builder
    public Message(String message, MessageType messageType, Set<String> reads, SiteUser sender, Chatroom chatroom) {
        this.message = message;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
        this.messageType = messageType;
        this.reads = reads;
        this.sender = sender;
        this.chatroom = chatroom;
    }
}
