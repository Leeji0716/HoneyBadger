package com.team.HoneyBadger.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class Department {
    // 그룹
    @Id
    @Setter(AccessLevel.NONE)
    private String name;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    @OneToMany(mappedBy = "department", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SiteUser> users;
    @ManyToOne(fetch = FetchType.LAZY)
    private Department parent;
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Department> child;

    @Builder
    public Department(String name, Department parent) {
        this.name = name;
        this.parent = parent;
        this.createDate = LocalDateTime.now();
        this.modifyDate = createDate;
//        this.users = new ArrayList<>();
    }
}
