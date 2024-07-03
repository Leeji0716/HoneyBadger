package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class VoteContent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;
    private Set<String> voters;
    @ManyToOne(fetch = FetchType.LAZY)
    private Vote vote;

    @Builder
    public VoteContent(String content, Set<String> voters, Vote vote) {
        this.content = content;
        this.voters = voters;
        this.vote = vote;
    }
}
