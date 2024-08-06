package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.PasswordChangeDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.DTO.UserInfoRequestDTO;
import com.team.HoneyBadger.DTO.UserResponseDTO;
import com.team.HoneyBadger.Exception.DataDuplicateException;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.DataNotSameException;
import com.team.HoneyBadger.Exception.InvalidFileTypeException;
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
@RequestMapping("/api/user")
public class UserController {

    private final MultiService multiService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestHeader("Authorization") String accessToken, @RequestBody UserInfoRequestDTO requestDTO) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                UserResponseDTO dto = multiService.signup(requestDTO);
                return ResponseEntity.status(HttpStatus.OK).body(dto);
            } catch (DataDuplicateException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
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
                UserResponseDTO siteUser = multiService.getUser(username);
                return ResponseEntity.status(HttpStatus.OK).body(siteUser);
            } catch (DataDuplicateException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
    }

    @PutMapping("/profile_image")
    public ResponseEntity<?> updateProfileImage(@RequestHeader("Authorization") String accessToken, MultipartFile file) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                UserResponseDTO userResponseDTO = this.multiService.updateProfile(tokenDTO.username(), file);
                return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
            } catch (InvalidFileTypeException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("올바르지 않은 파일입니다.");
            }
        } else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping("/profile_image")
    public ResponseEntity<?> deleteProfileImage(@RequestHeader("Authorization") String accessToken) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                UserResponseDTO userResponseDTO = this.multiService.deleteProfile(tokenDTO.username());
                return ResponseEntity.status(HttpStatus.OK).body(userResponseDTO);
            } catch (InvalidFileTypeException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/usernames") //로그인한 유저를 제외한 모든 유저 리스트 --> 나중에 수정 필요함.
    public ResponseEntity<?> getUsersAll(@RequestHeader("Authorization") String accessToken) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                List<UserResponseDTO> usernames = multiService.getAllUser(tokenDTO.username());
                return ResponseEntity.status(HttpStatus.OK).body(usernames);
            } catch (DataDuplicateException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/search")
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String accessToken, @RequestHeader(value = "Keyword", defaultValue = "") String keyword, @RequestHeader(value = "Page", defaultValue = "0") int page, @RequestHeader(value = "Size", defaultValue = "10") int size) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                Page<UserResponseDTO> pageDTO = multiService.getUsers(keyword != null ? URLDecoder.decode(keyword, StandardCharsets.UTF_8) : "", page, size);
                return ResponseEntity.status(HttpStatus.OK).body(pageDTO);
            } catch (DataDuplicateException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
    }

    @PutMapping
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String accessToken, @RequestBody PasswordChangeDTO passwordChangeDTO) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                multiService.changePassword(tokenDTO.username(), passwordChangeDTO);
                return ResponseEntity.status(HttpStatus.OK).body("OK");
            } catch (DataNotSameException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
    }

    @PutMapping("/info")
    public ResponseEntity<?> changeInfo(@RequestHeader("Authorization") String accessToken, @RequestBody UserInfoRequestDTO userInfoRequestDTO) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                UserResponseDTO dto = multiService.changeUser(userInfoRequestDTO);
                return ResponseEntity.status(HttpStatus.OK).body(dto);
            } catch (DataNotSameException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
    }

    @PutMapping("/status")
    public ResponseEntity<?> changeInfo(@RequestHeader("Authorization") String accessToken, @RequestHeader("Username") String username) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            try {
                UserResponseDTO dto = multiService.changeUserStatus(username);
                return ResponseEntity.status(HttpStatus.OK).body(dto);
            } catch (DataNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            }
        } else return tokenDTO.getResponseEntity();
    }

    @DeleteMapping("/temp")
    public ResponseEntity<?> deleteTempFiles(@RequestHeader("Authorization") String accessToken) {
        TokenDTO tokenDTO = this.multiService.checkToken(accessToken);

        if (tokenDTO.isOK()) {

            multiService.deleteUserTemp(tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body("deleted");
        } else return tokenDTO.getResponseEntity();
    }
}