package com.team.HoneyBadger.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Facility {
    @Id
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder
    public Facility(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
