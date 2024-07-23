package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.FileResponseDTO;
import com.team.HoneyBadger.DTO.FolderResponseDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Enum.FileOrder;
import com.team.HoneyBadger.Enum.FileType;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileSystemController {
    private final MultiService multiService;

    @GetMapping("/list")
    public ResponseEntity<?> getFiles(@RequestHeader("Authorization") String accessToken, @RequestHeader("Location") String location, @RequestHeader(value = "Page", defaultValue = "0") int page, @RequestHeader(value = "Type", defaultValue = "-1") int type, @RequestHeader(value = "Order", defaultValue = "0") int order) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            Page<FileResponseDTO> dtos = multiService.getStorageFiles(URLDecoder.decode(location, StandardCharsets.UTF_8), page, type >= 0 ? FileType.values()[type] : null, FileOrder.values()[order]);
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
            List<FolderResponseDTO> dto = multiService.getFileFolders(URLDecoder.decode(location, StandardCharsets.UTF_8));
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
            FileResponseDTO dto = multiService.getStorageFile(URLDecoder.decode(location, StandardCharsets.UTF_8));
            return ResponseEntity.status(HttpStatus.OK).body(dto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file error");
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    @PostMapping("/folder")
    public ResponseEntity<?> createFolder(@RequestHeader("Authorization") String accessToken, @RequestHeader("Location") String location, @RequestHeader("Base") String base) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.createFolder(URLDecoder.decode(location, StandardCharsets.UTF_8),URLDecoder.decode(base, StandardCharsets.UTF_8));
            return ResponseEntity.status(HttpStatus.OK).body("created");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("file error");
        }catch(NotAllowedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }
}
