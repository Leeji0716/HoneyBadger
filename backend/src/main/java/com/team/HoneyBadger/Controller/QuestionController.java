package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.QuestionDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question")
public class QuestionController {
    private final MultiService multiService;

    @PostMapping
    public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO requestDto) {
        this.multiService.createQuestion(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body("created");
    }

    @GetMapping
    public ResponseEntity<?> getList(@RequestHeader("page") int page, @RequestHeader(value = "keyword", required = false) String keyword) {
        Page<QuestionDTO> list = this.multiService.getQuestions(page, keyword != null ? URLDecoder.decode(keyword, StandardCharsets.UTF_8) : null);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PutMapping
    public ResponseEntity<?> createAnswer(@RequestBody QuestionDTO requestDto) {
        try {
            QuestionDTO questionDTO= this.multiService.createAnswer(requestDto);
            return ResponseEntity.status(HttpStatus.OK).body(questionDTO);
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkPassword(@RequestBody QuestionDTO requestDto) {
        try {
            boolean result = this.multiService.checkQuestion(requestDto);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
