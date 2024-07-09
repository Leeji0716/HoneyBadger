package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MessageReservation {
    // 채팅 예약 메시지
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(columnDefinition = "LONGTEXT")
    private String message;
    private LocalDateTime sendDate;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private int messageType;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender;
    @ManyToOne(fetch = FetchType.LAZY)
    private Chatroom chatroom;

    @Builder
    public MessageReservation(String message, LocalDateTime sendDate, int messageType, SiteUser sender, Chatroom chatroom) {
        this.message = message;
        this.sendDate = sendDate;
        this.messageType = messageType;
        this.sender = sender;
        this.chatroom = chatroom;
        this.createDate=LocalDateTime.now();
        this.modifyDate=createDate;
    }
}
