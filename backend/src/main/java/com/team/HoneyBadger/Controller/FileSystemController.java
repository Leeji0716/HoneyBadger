package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.FileResponseDTO;
import com.team.HoneyBadger.DTO.FolderResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileSystemController {
    private final MultiService multiService;

    @GetMapping("/list")
    public ResponseEntity<?> getFiles(@RequestHeader("Authorization") String accessToken, @RequestHeader("Location") String location, @RequestHeader(value = "Page", defaultValue = "0") int page) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            Page<FileResponseDTO> dtos = multiService.getStorageFiles(location, page);
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file error");
        } catch (DataNotFoundException | NotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping("/folders")
    public ResponseEntity<?> getFolders(@RequestHeader("Authorization") String accessToken, @RequestHeader("Location") String location) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            List<FolderResponseDTO> dto = multiService.getFileFolders(location);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @GetMapping
    public ResponseEntity<?> getFile(@RequestHeader("Authorization") String accessToken, @RequestHeader("Location") String location) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            FileResponseDTO dto = multiService.getStorageFile(location);
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file error");
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }
}
