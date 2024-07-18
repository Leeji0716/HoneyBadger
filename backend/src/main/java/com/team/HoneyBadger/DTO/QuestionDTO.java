package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record QuestionDTO(Long id, String title, String content, String answer, String author, String password,
                              Long createDate, Long modifyDate, boolean lock) {

}
