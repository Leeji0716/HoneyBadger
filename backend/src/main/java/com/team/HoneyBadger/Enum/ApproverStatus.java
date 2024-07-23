package com.team.HoneyBadger.Enum;

import lombok.Getter;

@Getter
public enum ApproverStatus {
    NOT_READ("안 읽음"),READY("결재 대기중"), ALLOW("허가"), DENY("반환")
    //
    ;
    private final String name;

    ApproverStatus(String name) {
        this.name = name;
    }
}
