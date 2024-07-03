package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;

import lombok.AccessLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EmailReceiver {
    // 이메일 받는 사람
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Email email;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser receiver;
    private boolean status; // 읽음 여부

    @Builder
    public EmailReceiver(Email email, SiteUser receiver) {
        this.email = email;
        this.receiver = receiver;
        this.status = false;
    }
}
