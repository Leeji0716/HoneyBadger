package com.team.HoneyBadger.Enum;

import lombok.Getter;

@Getter
public enum OsType {
    Window("C:/web/honeybadger"), Linux("/home/ubuntu/honeybadger/data")
    //
    ;
    private final String loc;

    OsType(String loc) {
        this.loc = loc;
    }

    public static OsType getOsType() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("window")) return Window;
        if (osName.contains("linux")) return Linux;
        System.out.println(osName);
        return null;
    }
}
