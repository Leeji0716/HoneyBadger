package com.team.HoneyBadger.Entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFacilityReservation is a Querydsl query type for FacilityReservation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFacilityReservation extends EntityPathBase<FacilityReservation> {

    private static final long serialVersionUID = -835248474L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFacilityReservation facilityReservation = new QFacilityReservation("facilityReservation");

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final QFacility facility;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final QSiteUser user;

    public QFacilityReservation(String variable) {
        this(FacilityReservation.class, forVariable(variable), INITS);
    }

    public QFacilityReservation(Path<? extends FacilityReservation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFacilityReservation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFacilityReservation(PathMetadata metadata, PathInits inits) {
        this(FacilityReservation.class, metadata, inits);
    }

    public QFacilityReservation(Class<? extends FacilityReservation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.facility = inits.isInitialized("facility") ? new QFacility(forProperty("facility")) : null;
        this.user = inits.isInitialized("user") ? new QSiteUser(forProperty("user"), inits.get("user")) : null;
    }

}

