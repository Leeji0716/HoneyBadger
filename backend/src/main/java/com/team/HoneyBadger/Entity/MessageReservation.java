package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.MessageType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class MessageReservation {
    // 메시지 예약
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender;
    @ManyToOne(fetch = FetchType.LAZY)
    private Chatroom chatroom;
    private LocalDateTime sendTime;
    private MessageType messageType;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
@Builder
    public MessageReservation(String message, SiteUser sender, Chatroom chatroom, LocalDateTime sendTime, MessageType messageType) {
        this.message = message;
        this.sender = sender;
        this.chatroom = chatroom;
        this.sendTime = sendTime;
        this.messageType = messageType;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
    }
}
