package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {
    Page<Question> findByKeyword(String keyword, Pageable pageable);
}
