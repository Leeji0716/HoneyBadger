package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class EmailReceiver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private Email email;
    @ManyToOne(fetch = FetchType.EAGER)
    private SiteUser receiver;
    private boolean status;

    @Builder
    public EmailReceiver(Email email, SiteUser receiver) {
        this.email = email;
        this.receiver = receiver;
        this.status = false;
    }
}
