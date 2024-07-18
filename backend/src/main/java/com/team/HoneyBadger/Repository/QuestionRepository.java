package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Question;
import com.team.HoneyBadger.Repository.Custom.QuestionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, QuestionRepositoryCustom {
}
