package com.team.HoneyBadger.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Entity
@Getter
@Setter
public class MultiKey {
    @Id
    @Column(columnDefinition = "TEXT")
    @Setter(AccessLevel.NONE)
    private String k;
    private HashSet<String> keys;

    @Builder
    public MultiKey(String k, HashSet<String> keys) {
        this.k = k;
        this.keys = keys;
    }
}
