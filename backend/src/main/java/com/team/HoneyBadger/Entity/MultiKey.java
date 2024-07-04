package com.team.HoneyBadger.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
