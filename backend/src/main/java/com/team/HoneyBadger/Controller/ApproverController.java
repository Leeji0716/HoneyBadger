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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/approver")
public class ApproverController {
    private final MultiService multiService;


    @PostMapping // 승인 기능
    public ResponseEntity<?> acceptApprover(@RequestHeader("Authorization") String accessToken, @RequestHeader("approvalId") Long approvalId, @RequestHeader boolean Binary) {
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if (tokenDTO.isOK ()) try {
            ApprovalResponseDTO approvalResponseDTO = multiService.acceptApprover (approvalId, tokenDTO.username (), Binary);
            return ResponseEntity.status (HttpStatus.OK).body (approvalResponseDTO);
        } catch (NotAllowedException ex) {
            return ResponseEntity.status (HttpStatus.FORBIDDEN).body (ex.getMessage ());
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        }
        else return tokenDTO.getResponseEntity ();
    }



}
