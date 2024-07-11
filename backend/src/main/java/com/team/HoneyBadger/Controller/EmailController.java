package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.Config.Exception.DataNotFoundException;
import com.team.HoneyBadger.DTO.EmailRequestDTO;
import com.team.HoneyBadger.DTO.EmailResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Enum.EmailStatus;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/list") //유저에 대한 모든 이메일
    public ResponseEntity<?> getEmailsForUser(@RequestHeader("Authorization") String accessToken, @RequestHeader("status") int status) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<EmailResponseDTO> emailResponseDTOList = multiService.getEmailsForUser(tokenDTO.username(), EmailStatus.values()[status]);
            return ResponseEntity.status(HttpStatus.OK).body(emailResponseDTOList);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> handleFileUpload(@RequestHeader("Authorization") String accessToken, MultipartFile file) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (file.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("파일을 선택해주세요.");
        if (tokenDTO.isOK()) try {
            String fileName = multiService.emailContentUpload(tokenDTO.username(), file);
            return ResponseEntity.status(HttpStatus.OK).body(fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일 업로드 실패");
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping
    public ResponseEntity<?> getEmail(@RequestHeader("Authorization") String accessToken, @RequestHeader Long emailId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            EmailResponseDTO emailResponseDTO = multiService.getEmailDTO(emailId);
            return ResponseEntity.status(HttpStatus.OK).body(emailResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();

    }

    @PostMapping
    public ResponseEntity<?> sendEmail(@RequestHeader("Authorization") String accessToken, EmailRequestDTO emailRequestDTO, //@RequestBody로 받아야 함.
                                       List<MultipartFile> attachments) { //파일은 다른 메서드로 분리해야 함.

        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            System.out.println("Received email send request");
            EmailResponseDTO emailResponseDTO = multiService.sendEmail(emailRequestDTO.title(), emailRequestDTO.content(), tokenDTO.username(), emailRequestDTO.receiverIds());
            return ResponseEntity.status(HttpStatus.OK).body(emailResponseDTO);
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file error");
        }
        else return tokenDTO.getResponseEntity();
    }

    @PostMapping("/read")
    public ResponseEntity<?> markEmailAsRead(@RequestHeader Long emailId, @RequestHeader String receiverId) {
        Boolean isRead = multiService.markEmailAsRead(emailId, receiverId);
        return ResponseEntity.status(HttpStatus.OK).body(isRead);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteEmail(@RequestHeader("Authorization") String accessToken, @RequestHeader Long emailId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            multiService.deleteEmail(emailId, tokenDTO.username());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return tokenDTO.getResponseEntity();
        }
    }
}