package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.CycleDTO;
import com.team.HoneyBadger.DTO.CycleResponseDTO;
import com.team.HoneyBadger.DTO.CycleRequestDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Enum.KeyPreset;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/personal")
public class PersonalCycleController {

    private final MultiService multiService;

    //개인일정 생성
    @PostMapping
    public ResponseEntity<?> PersonalCycle(@RequestHeader("Authorization") String accessToken, @RequestBody CycleRequestDTO cycleRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.createPersonalCycle(KeyPreset.UC.getValue(tokenDTO.username()), cycleRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    //개인일정 수정
    @PutMapping
    public ResponseEntity<?> personalCycle(@RequestHeader("Authorization") String accessToken, @RequestHeader("id") Long id, @RequestBody CycleRequestDTO cycleRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
           CycleDTO cycleDTO = multiService.updatePersonalCycle(KeyPreset.UC.getValue(tokenDTO.username()), id, cycleRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(cycleDTO);
        } catch (IllegalArgumentException | NotAllowedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    //개인일정 삭제
    @DeleteMapping
    public ResponseEntity<?> personalCycle(@RequestHeader("Authorization") String accessToken, @RequestHeader("id") Long id) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteCycle(KeyPreset.UC.getValue(tokenDTO.username()), id);
            return ResponseEntity.status(HttpStatus.OK).body("delete");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }


    //개인일정 리스트
    @GetMapping
    public ResponseEntity<?> myMonthCycle(@RequestHeader("Authorization") String accessToken, @RequestHeader("startDate") String startDate, @RequestHeader("endDate") String endDate) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            List<CycleResponseDTO> cycleResponseDTOList = multiService.getMyCycle(KeyPreset.UC.getValue(tokenDTO.username()), LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")), LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            return ResponseEntity.status(HttpStatus.OK).body(cycleResponseDTOList);
        } else return tokenDTO.getResponseEntity();
    }
//
//    //개인일정 태그 설정
//    @PostMapping("/tag")
//    public ResponseEntity<?> tagMake(@RequestHeader("Authorization") String accessToken, @RequestBody PersonalCycleRequestTagDTO personalCycleRequestTagDTO) {
//        TokenDTO tokenDTO = multiService.checkToken(accessToken);
//        if (tokenDTO.isOK()) try {
//            multiService.personalCycleTag(tokenDTO.username(), personalCycleRequestTagDTO.id(), personalCycleRequestTagDTO.tags());
//            return ResponseEntity.status(HttpStatus.OK).body("ok!");
//        } catch (NotAllowedException ex) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
//        } catch (DataNotFoundException ex) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//        }
//        else return tokenDTO.getResponseEntity();
//    }
//
//    //개인일정 태그리스트
//    @GetMapping("/tagList")
//    public ResponseEntity<?> tagList(@RequestHeader("Authorization") String accessToken, @RequestHeader("tag") String tag) {
//        TokenDTO tokenDTO = multiService.checkToken(accessToken);
//        if (tokenDTO.isOK()) {
//
//            List<PersonalCycleDTO> personalCycleDTOList = multiService.personalCycleTagList(tokenDTO.username(), URLDecoder.decode(tag, StandardCharsets.UTF_8));
//            return ResponseEntity.status(HttpStatus.OK).body(personalCycleDTOList);
//        } else return tokenDTO.getResponseEntity();
//    }
//
//    //개인일정 태그삭제
//    @PutMapping("/tagDelete")
//    public ResponseEntity<?> tagDelete(@RequestHeader("Authorization") String accessToken, @RequestBody PersonalCycleRequestTagDTO personalCycleRequestTagDTO) {
//        TokenDTO tokenDTO = multiService.checkToken(accessToken);
//        if (tokenDTO.isOK()) try {
//            multiService.deleteTag(tokenDTO.username(), personalCycleRequestTagDTO.id(), personalCycleRequestTagDTO.tag());
//            return ResponseEntity.status(HttpStatus.OK).body("tagDelete");
//        } catch (NotAllowedException ex) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
//        } catch (IllegalArgumentException | DataNotFoundException ex){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//        }
//        else return tokenDTO.getResponseEntity();
//    }
}
