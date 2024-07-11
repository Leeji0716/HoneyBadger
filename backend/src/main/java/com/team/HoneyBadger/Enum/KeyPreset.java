package com.team.HoneyBadger.Enum;

public enum KeyPreset {
    USER_PROFILE, USER_TEMP, USER_TEMP_MULTI, EMAIL_MULTI, EMAIL
    //
    ;

    public String getValue(String var) {
        return this.name() + "_" + var.toUpperCase();
    }

}
