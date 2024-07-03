package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
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
    @ManyToOne(fetch = FetchType.LAZY)
    private Group parent;
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Group> subGroups;

    @Builder
    public Group(String name) {
        this.name = name;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
        this.users = new ArrayList<>();
    }
}
