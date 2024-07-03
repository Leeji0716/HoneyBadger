package com.team.HoneyBadger.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class FacilityReservation {
    @Id
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private Facility facility;
    @ManyToOne(fetch = FetchType.LAZY)
    private SiteUser user;

    @Builder
    public FacilityReservation(LocalDateTime startDate, LocalDateTime endDate, Facility facility, SiteUser user) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.facility = facility;
        this.user = user;
    }
}

