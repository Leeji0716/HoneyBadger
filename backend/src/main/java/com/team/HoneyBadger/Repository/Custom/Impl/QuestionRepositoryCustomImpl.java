package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.QQuestion;
import com.team.HoneyBadger.Entity.Question;
import com.team.HoneyBadger.Repository.Custom.QuestionRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    QQuestion qQuestion = QQuestion.question;

    @Override
    public Page<Question> findByKeyword(String keyword, Pageable pageable) {
        List<Question> list;
        long total;
        if (keyword == null || keyword.isBlank()) {
            list = jpaQueryFactory.selectFrom(qQuestion).orderBy(qQuestion.createDate.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
            total = jpaQueryFactory.select(qQuestion).from(qQuestion).fetchCount();
        } else {
            list = jpaQueryFactory.selectFrom(qQuestion).where(qQuestion.author.contains(keyword).or(qQuestion.title.contains(keyword))).orderBy(qQuestion.createDate.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
            total = jpaQueryFactory.selectFrom(qQuestion).where(qQuestion.author.contains(keyword).or(qQuestion.title.contains(keyword))).fetchCount();
        }

        return new PageImpl<>(list, pageable, total);
    }
}
