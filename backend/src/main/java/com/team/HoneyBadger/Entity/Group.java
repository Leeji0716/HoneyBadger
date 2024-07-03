package com.team.HoneyBadger.Entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;

@Entity
@Getter
@Setter
public class Group {
    // 그룹
    @Id
    @Setter(AccessLevel.NONE)
    private String name;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SiteUser> users;

    @Builder
    public Group(String name) {
        this.name = name;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
        this.users = new ArrayList<>();
    }
}
