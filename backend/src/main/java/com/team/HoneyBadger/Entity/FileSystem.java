package com.team.HoneyBadger.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FileSystem {
    @Id
    @Setter(AccessLevel.NONE)
    private String k;
    @Column(columnDefinition = "LONGTEXT")
    private String v;

    @Builder
    public FileSystem(String k, String v) {
        this.k = k;
        this.v = v;
    }
}
