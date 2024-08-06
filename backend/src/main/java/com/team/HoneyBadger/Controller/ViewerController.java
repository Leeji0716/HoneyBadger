package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.ApprovalRequestDTO;
import com.team.HoneyBadger.DTO.ApprovalResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/viewer")
public class ViewerController {
    private final MultiService multiService;

    @PostMapping // 기안서 참조자 변경 (전송)
    public ResponseEntity<?> changeViewer(@RequestHeader("Authorization") String accessToken, @RequestHeader("approvalId") Long approvalId, @RequestBody ApprovalRequestDTO approvalRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken (accessToken);
        if (tokenDTO.isOK ()) try {
            ApprovalResponseDTO approvalResponseDTO = multiService.addViewer (approvalId, approvalRequestDTO, tokenDTO.username ());
            return ResponseEntity.status (HttpStatus.OK).body (approvalResponseDTO);
        } catch (NotAllowedException ex) {
            return ResponseEntity.status (HttpStatus.FORBIDDEN).body (ex.getMessage ());
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status (HttpStatus.NOT_FOUND).body (ex.getMessage ());
        }
        else return tokenDTO.getResponseEntity ();
    }
}
