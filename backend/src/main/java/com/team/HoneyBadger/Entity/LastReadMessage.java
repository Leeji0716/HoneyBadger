package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LastReadMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser siteUser;
    @ManyToOne(fetch = FetchType.LAZY)
    private Chatroom chatroom;

    private Long lastReadMessage;
}
