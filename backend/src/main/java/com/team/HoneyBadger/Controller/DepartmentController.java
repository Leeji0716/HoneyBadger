package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.DepartmentRequestDTO;
import com.team.HoneyBadger.DTO.DepartmentTopResponseDTO;
import com.team.HoneyBadger.DTO.DepartmentUserResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Exception.DataDuplicateException;
import com.team.HoneyBadger.Exception.RelatedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final MultiService multiService;

    @GetMapping
    public ResponseEntity<?> getDepartment(@RequestHeader("Authorization") String accessToken) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            List<DepartmentTopResponseDTO> list = this.multiService.getDepartmentTree();
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } else return tokenDTO.getResponseEntity();
    }

    @PostMapping
    public ResponseEntity<?> saveDepartment(@RequestHeader("Authorization") String accessToken, @RequestBody DepartmentRequestDTO departmentRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<DepartmentTopResponseDTO> list = this.multiService.createDepartment(tokenDTO.username(), departmentRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("파일 저장에 오류가 발생");
        } catch (DataDuplicateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PostMapping("/img")
    public ResponseEntity<?> saveDepartmentImage(@RequestHeader("Authorization") String accessToken, MultipartFile file) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            String url = this.multiService.saveDepartmentImage(tokenDTO.username(), file);
            return ResponseEntity.status(HttpStatus.OK).body(url);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("파일 저장에 오류가 발생");
        }
        else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteDepartment(@RequestHeader("Authorization") String accessToken, @RequestHeader("DepartmentId") String departmentId) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<DepartmentTopResponseDTO> list = this.multiService.deleteDepartment(URLDecoder.decode(departmentId, StandardCharsets.UTF_8));
            return ResponseEntity.status(HttpStatus.OK).body(list);
        } catch (RelatedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/users")
    public ResponseEntity<?> getDepartmentUser(@RequestHeader("Authorization") String accessToken, @RequestHeader(value = "DepartmentId", required = false) String departmentId) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                DepartmentUserResponseDTO response = multiService.getDepartmentUsers(departmentId != null ? URLDecoder.decode(departmentId, StandardCharsets.UTF_8) : departmentId);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } catch (DataDuplicateException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
    }

}
