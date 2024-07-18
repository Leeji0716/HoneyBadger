package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Question;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    public Question get(Long id) {
        return questionRepository.findById(id).orElseThrow(() -> new DataNotFoundException("question not found"));
    }

    public Page<Question> getList(int page, String keyword) {
        return questionRepository.findByKeyword(keyword, PageRequest.of(page, 10));
    }

    public Question save(String title, String content, String author, String password, boolean lock) {
        Question question = Question.builder().title(title).content(content).author(author).password(password != null ? passwordEncoder.encode(password) : null).lock(lock).build();
        return questionRepository.save(question);
    }

    public Question update(Question question, String answer) {
        question.setAnswer(answer);
        return questionRepository.save(question);
    }

    public boolean checkPassword(Question question, String password) {
        return question.getPassword() == null || passwordEncoder.matches(password, question.getPassword());
    }

}
