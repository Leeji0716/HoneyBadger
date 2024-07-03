package com.team.HoneyBadger.Enum;

import lombok.Getter;

@Getter
public enum AttendanceType {
    ATTENDANCE("출근"), LATENESS("지각"), ABSENCE("결근"), VACATION("휴가"), ETC("기타")
    //
    ;
    private final String name;

    AttendanceType(String name) {
        this.name = name;
    }
}
