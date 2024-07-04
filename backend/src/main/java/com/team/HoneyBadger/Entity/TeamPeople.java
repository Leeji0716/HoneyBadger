package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.TeamRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TeamPeople {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private TeamRole role;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser user;
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @Builder
    public TeamPeople(TeamRole role, SiteUser user, Team team) {
        this.role = role;
        this.user = user;
        this.team = team;
    }
}
