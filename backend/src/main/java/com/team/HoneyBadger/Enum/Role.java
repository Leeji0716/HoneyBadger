package com.team.HoneyBadger.Enum;

import lombok.Getter;

@Getter
public enum Role {
    PRESIDENT("사장"), VICE_PRESIDENT("부사장"), EXECUTIVE_MANGING_DIRECTOR("전무"), MANGING_DIRECTOR("상무"), EXECUTIVE_DIRECTOR("이사"), OUTSIDE_DIRECTOR("사외 이사"), ADVISING_DIRECTOR("고문"), AUDITING_DIRECTOR("감사"), GENERAL_MANGER("부장"), DEPUTY_GENERAL_MANAGER("과장"), ASSISTANT_MANAGER("대리"), SENIOR_STAFF("주임"), STAFF("직원")
    //
    ;
    private final String name;

    Role(String name) {
        this.name = name;
    }
}
