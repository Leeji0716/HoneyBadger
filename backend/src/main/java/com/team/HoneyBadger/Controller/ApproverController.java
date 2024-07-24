package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.ApprovalResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Entity.Approver;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approver")
public class ApproverController {
    private final MultiService multiService;

    @PutMapping // 승인자 상태 변경
    public ResponseEntity<?> approverStatus(@RequestHeader("Authorization") String accessToken, @RequestHeader("approvalId") Long approvalId) {
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




}
