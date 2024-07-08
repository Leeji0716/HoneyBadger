package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.SignupRequestDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.DTO.UserResponseDTO;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataDuplicateException;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Service.Module.UserService;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final MultiService multiService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        try {
            multiService.signup(signupRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (DataDuplicateException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String accessToken) {
        try {
            TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
            if (tokenDTO.isOK()) {
                String username = tokenDTO.username();
                UserResponseDTO userResponseDTO = this.multiService.getProfile(username);
                return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
            } else return tokenDTO.getResponseEntity();
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String accessToken, @RequestHeader String username) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                SiteUser siteUser = userService.get(username);
                return ResponseEntity.status(HttpStatus.OK).body(siteUser);
            } catch (DataDuplicateException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
    }
}