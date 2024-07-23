package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> approvalGet(@RequestHeader("Authorization") String accessToken, @RequestHeader("approvalId") Long approvalId) {
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if (tokenDTO.isOK ()) try {
            ApprovalResponseDTO approvalResponseDTO = multiService.getApproval (approvalId);
            return ResponseEntity.status (HttpStatus.OK).body (approvalResponseDTO);
        } catch (NotAllowedException ex) {
            return ResponseEntity.status (HttpStatus.FORBIDDEN).body (ex.getMessage ());
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        }
        else return tokenDTO.getResponseEntity ();
    }

}
