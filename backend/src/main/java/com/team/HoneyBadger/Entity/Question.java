package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    @Column(columnDefinition = "LONGTEXT")
    private String answer;

    private String author;
    @Column(columnDefinition = "TEXT")
    private String password;
    @Setter(AccessLevel.NONE)
    private boolean isLock;
    @Setter(AccessLevel.NONE)
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @Builder
    public Question(String title, String content, String author, String password,boolean lock) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.password = password;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
        this.isLock =lock;
    }
}
