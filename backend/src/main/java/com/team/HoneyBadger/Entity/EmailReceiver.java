package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EmailReceiver {
    // 이메일 받는 사람
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Email email;
    @ManyToOne(fetch = FetchType.EAGER)
    private SiteUser receiver;
    private boolean status; // 읽음 여부

    @Builder
    public EmailReceiver(Email email, SiteUser receiver) {
        this.email = email;
        this.receiver = receiver;
        this.status = false;
    }
}
