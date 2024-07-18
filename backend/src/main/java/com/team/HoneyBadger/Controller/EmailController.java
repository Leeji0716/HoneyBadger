package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.EmailReadRequestDTO;
import com.team.HoneyBadger.DTO.EmailRequestDTO;
import com.team.HoneyBadger.DTO.EmailResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final MultiService multiService;

    @GetMapping("/list") // 유저에 대한 모든 이메일
    public ResponseEntity<?> getEmailsForUser(@RequestHeader("Authorization") String accessToken, @RequestHeader("status") int status, @RequestHeader("Page") int page) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                Page<Object> emailResponseDTOList = multiService.getEmailsForUser(tokenDTO.username(), status, page);
                return ResponseEntity.status(HttpStatus.OK).body(emailResponseDTOList);
            }catch (DataNotFoundException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }catch (NotAllowedException e){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @PostMapping("/upload") // 이메일 내용 업로드
    public ResponseEntity<?> handleFileUpload(@RequestHeader("Authorization") String accessToken, MultipartFile file) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                String fileName = multiService.emailContentUpload(tokenDTO.username(), file);
                return ResponseEntity.status(HttpStatus.OK).body(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 실패: " + e.getMessage());
            } catch (DataNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @PostMapping("/files") // 이메일 파일 저장
    public ResponseEntity<?> saveFiles(@RequestHeader("Authorization") String accessToken, @RequestHeader("email_id") Long email_id, List<MultipartFile> attachments) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                multiService.emailFilesUpload(email_id, attachments);
                return ResponseEntity.status(HttpStatus.OK).body("파일 업로드 성공");
            } catch (IOException ex) {
                ex.printStackTrace();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("파일 업로드 실패: " + ex.getMessage());
            }
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @GetMapping //이메일 디테일
    public ResponseEntity<?> getEmail(@RequestHeader("Authorization") String accessToken, @RequestHeader Long emailId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                EmailResponseDTO emailResponseDTO = multiService.getEmailDTO(emailId, tokenDTO.username());
                return ResponseEntity.status(HttpStatus.OK).body(emailResponseDTO);
            } catch (RuntimeException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            }
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @PostMapping // 이메일 보내기
    public ResponseEntity<?> sendEmail(@RequestHeader("Authorization") String accessToken, @RequestBody EmailRequestDTO emailRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                Long emailId = multiService.sendEmail(emailRequestDTO.title(), emailRequestDTO.content(), tokenDTO.username(), emailRequestDTO.receiverIds());
                return ResponseEntity.status(HttpStatus.OK).body(emailId);
            } catch (DataNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("파일 오류: " + ex.getMessage());
            }
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @PutMapping("/read") // 이메일 읽음 처리
    public ResponseEntity<?> markEmailAsRead(@RequestBody EmailReadRequestDTO emailReadRequestDTO) {
        try {
            EmailResponseDTO emailResponseDTO = multiService.read(emailReadRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(emailResponseDTO);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping // 이메일 삭제
    public ResponseEntity<?> deleteEmail(@RequestHeader("Authorization") String accessToken, @RequestHeader Long emailId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                multiService.deleteEmail(emailId, tokenDTO.username());
                return ResponseEntity.status(HttpStatus.OK).body("이메일 삭제 성공");
            } catch (RuntimeException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            }
        } else {
            return tokenDTO.getResponseEntity();
        }
    }
}