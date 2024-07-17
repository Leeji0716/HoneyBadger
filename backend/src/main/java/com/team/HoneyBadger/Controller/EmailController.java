package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.EmailReadRequestDTO;
import com.team.HoneyBadger.DTO.EmailRequestDTO;
import com.team.HoneyBadger.DTO.EmailResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
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
            } catch (DataNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
        } else {
            return tokenDTO.getResponseEntity();
        }
    }

    @PostMapping("/upload") //이메일 파일 업로드
    public ResponseEntity<?> handleFileUpload(@RequestHeader("Authorization") String accessToken, MultipartFile file) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (file.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 선택해주세요.");
        if (tokenDTO.isOK()) try {
            String fileName = multiService.fileUpload(tokenDTO.username(), file);
            return ResponseEntity.status(HttpStatus.OK).body(fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 실패");
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        else return tokenDTO.getResponseEntity();
    }

    @PostMapping("/files")
    public ResponseEntity<?> saveFiles(@RequestHeader("Authorization") String accessToken, @RequestHeader("email_id") Long email_id, List<MultipartFile> attachments) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                multiService.emailFilesUpload(email_id, attachments);
                return ResponseEntity.status(HttpStatus.OK).body("okay");
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("files not uploaded");
            }
        } else
            return tokenDTO.getResponseEntity();
    }

    @GetMapping
    public ResponseEntity<?> getEmail(@RequestHeader("Authorization") String accessToken, @RequestHeader Long emailId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            EmailResponseDTO emailResponseDTO = multiService.getEmailDTO(emailId, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(emailResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();

    }

    @PostMapping
    public ResponseEntity<?> sendEmail(@RequestHeader("Authorization") String accessToken, @RequestBody EmailRequestDTO emailRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            Long emailId = multiService.sendEmail(emailRequestDTO.title(), emailRequestDTO.content(), tokenDTO.username(), emailRequestDTO.receiverIds());
            return ResponseEntity.status(HttpStatus.OK).body(emailId);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("files error");
        }
        else return tokenDTO.getResponseEntity();
    }

    @PutMapping("/read") //메세지 읽음 처리
    public ResponseEntity<?> markEmailAsRead(@RequestHeader("Authorization") String accessToken, @RequestBody EmailReadRequestDTO emailReadRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        EmailResponseDTO emailResponseDTO = multiService.read(emailReadRequestDTO, tokenDTO.username());
        return ResponseEntity.status(HttpStatus.OK).body(emailResponseDTO);
    }

    @DeleteMapping //메세지 삭제
    public ResponseEntity<?> deleteEmail(@RequestHeader("Authorization") String accessToken, @RequestHeader Long emailId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteEmail(emailId, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body("email DELETE SUCCESS");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN : " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("BAD_REQUEST : " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL_SERVER_ERROR : " + ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }
}