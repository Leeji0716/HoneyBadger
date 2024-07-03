package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Group {
    // 그룹
    @Id
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private Group parent;
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Group> groupList;
    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SiteUser> userList;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @Builder
    public Group(String name, Group parent) {
        this.name = name;
        this.parent = parent;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
    }
}
