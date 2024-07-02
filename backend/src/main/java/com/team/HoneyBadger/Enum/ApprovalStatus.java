package com.team.HoneyBadger.Enum;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
    UNREAD("안 읽음"), READY("결재 대기 중"), APPROVE("결재 완료"), RETURN("반려")
    //
    ;
    private final String name;

    ApprovalStatus(String name) {
        this.name = name;
    }
}
