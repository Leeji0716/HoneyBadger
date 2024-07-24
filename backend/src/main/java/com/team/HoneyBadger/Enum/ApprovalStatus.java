package com.team.HoneyBadger.Enum;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
   READY("결재 대기중"), RUNNING("결재 중"), ALLOW("허가"), DENY("반환")
    //
    ;
    private final String name;

    ApprovalStatus(String name) {
        this.name = name;
    }
}
