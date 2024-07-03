package com.team.HoneyBadger.Enum;

public enum KeyPreset {
    EMAIL_FILE_, EMAIL_MULTI
    //
    ;

    public String getValue(String var) {
        return this.name() + var.toUpperCase();
    }

}
