package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class EmailReceiver {
    // 이메일 받은 사람
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser receiver;
    @ManyToOne
    private Email email;
    private boolean status;
    @Builder
    public EmailReceiver(SiteUser receiver, Email email) {
        this.receiver = receiver;
        this.email = email;
        this.status = false;
    }
}
