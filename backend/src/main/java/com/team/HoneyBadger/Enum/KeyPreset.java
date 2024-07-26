package com.team.HoneyBadger.Enum;

public enum KeyPreset {
    USER_PROFILE, USER_TEMP, USER_TEMP_MULTI, //
    EMAIL_MULTI, EMAIL, EMAIL_ORIGIN, //
    EMAIL_RESERVATION_MULTI, EMAIL_RESERVATION, EMAIL_RESERVATION_ORIGIN, EMAIL_RESERVATION_MULTI_TEMP, EMAIL_RESERVATION_TEMP,//
    DEPARTMENT_PROFILE,//
    UC, DC, TC, //
    APPROVAL_MULTI, APPROVAL, APPROVAL_ORIGIN //
    ;

    public String getValue(String var) {
        return this.name() + "_" + var.toUpperCase();
    }

}
