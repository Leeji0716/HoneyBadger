package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.*;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approval")
public class ApprovalController {
    private final MultiService multiService;


    @PostMapping // 기안서 생성
    public ResponseEntity<?> approvalCreate(@RequestHeader("Authorization") String accessToken, @RequestBody ApprovalRequestDTO approvalRequestDTO){
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if(tokenDTO.isOK()) try {
            ApprovalResponseDTO approvalResponseDTO = multiService.createApproval (approvalRequestDTO,tokenDTO.username ());
            return ResponseEntity.status (HttpStatus.OK).body (approvalResponseDTO);
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping // 기안서 삭제
    public ResponseEntity<?> approvalDelete(@RequestHeader("Authorization") String accessToken, @RequestHeader("approvalId") Long approvalId){
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteApproval(approvalId);
            return ResponseEntity.status(HttpStatus.OK).body("DELETE SUCCESS");
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }


    @GetMapping // 기안서 가져오기
    public ResponseEntity<?> approvalGet(@RequestHeader("Authorization") String accessToken,
                                         @RequestHeader("approvalId") Long approvalId) {
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if (tokenDTO.isOK ()) try {
            ApprovalResponseDTO approvalResponseDTO = multiService.addApproval (approvalId);
            return ResponseEntity.status (HttpStatus.OK).body (approvalResponseDTO);
        } catch (NotAllowedException ex) {
            return ResponseEntity.status (HttpStatus.FORBIDDEN).body (ex.getMessage ());
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        }
        else return tokenDTO.getResponseEntity ();
    }

    @PutMapping("/updateRead") // 기안서 읽음 처리
    public ResponseEntity<?> approvalRead(@RequestHeader("Authorization") String accessToken, @RequestHeader("approvalId") Long approvalId) {
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if (tokenDTO.isOK ()) try {
            ApprovalResponseDTO approvalResponseDTO = multiService.addReader(approvalId, tokenDTO.username ());
            return ResponseEntity.status (HttpStatus.OK).body (approvalResponseDTO);
        } catch (NotAllowedException ex) {
            return ResponseEntity.status (HttpStatus.FORBIDDEN).body (ex.getMessage ());
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        }
        else return tokenDTO.getResponseEntity ();
    }

    @GetMapping("/list") // 기안서 리스트 가져오기
    public ResponseEntity<?> approvalList(@RequestHeader("Authorization") String accessToken,
                                          @RequestHeader(value = "keyword", defaultValue = "") String keyword,
                                          @RequestHeader(value = "Page", required = false) Integer page) {
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if (page == null || page < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 페이지를 찾을 수 없습니다.");
        }
        if (tokenDTO.isOK ()) try {
            Page<ApprovalResponseDTO> approvalResponseDTOList = multiService.getApprovalList (tokenDTO.username (), URLDecoder.decode(keyword, StandardCharsets.UTF_8), page);
            return ResponseEntity.status (HttpStatus.OK).body (approvalResponseDTOList);
        } catch (NotAllowedException ex) {
            return ResponseEntity.status (HttpStatus.FORBIDDEN).body (ex.getMessage ());
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        }
        else return tokenDTO.getResponseEntity ();
    }

    @PostMapping("/files") // 기안 첨부 파일 저장
    public ResponseEntity<?> saveFiles(@RequestHeader("Authorization") String accessToken, @RequestHeader("approvalId") Long approvalId, List<MultipartFile> attachments) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                multiService.approvalFilesUpload(approvalId, attachments);
                return ResponseEntity.status(HttpStatus.OK).body("파일 업로드 성공");
            } catch (IOException ex) {
                ex.printStackTrace();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("파일 업로드 실패: " + ex.getMessage());
            }
        } else {
            return tokenDTO.getResponseEntity();
        }
    }
}
