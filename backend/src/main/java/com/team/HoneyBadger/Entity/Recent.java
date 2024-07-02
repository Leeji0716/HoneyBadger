package com.team.HoneyBadger.Entity;

import com.team.HoneyBadger.Enum.RecentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Recent {
    // 최근 보낸 사람 목록
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser sender;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser receiver;
    private RecentStatus status;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
}
