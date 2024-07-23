package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate nowDate;
    @Column(length = 100)
    private String title;

    @Builder
    public Holiday(LocalDate date, String title) {
        this.nowDate = date;
        this.title = title;
    }
}
