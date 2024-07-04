package com.team.HoneyBadger.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class MultiKey {
    @Id
    @Setter(AccessLevel.NONE)
    private String k;
    private List<String> keyValues;

    @Builder
    public MultiKey(String k, List<String> keyValues) {
        this.k = k;
        this.keyValues = keyValues;
    }
}
