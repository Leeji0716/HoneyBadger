package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cycle")
public class CycleController {

    private final MultiService multiService;

    //개인일정 생성
    @PostMapping
    public ResponseEntity<?> PersonalCycle(@RequestHeader("Authorization") String accessToken, @RequestHeader("status") int status, @RequestBody CycleRequestDTO cycleRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.createPersonalCycle(status, tokenDTO.username(), cycleRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    //개인일정 수정
    @PutMapping
    public ResponseEntity<?> personalCycle(@RequestHeader("Authorization") String accessToken, @RequestHeader("id") Long id, @RequestHeader("status") int status, @RequestBody CycleRequestDTO cycleRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            CycleDTO cycleDTO = multiService.updatePersonalCycle(tokenDTO.username(),status, id, cycleRequestDTO);
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
            multiService.deleteCycle(id);
            return ResponseEntity.status(HttpStatus.OK).body("delete");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }


    //개인일정 리스트
    @GetMapping
    public ResponseEntity<?> myMonthCycle(@RequestHeader("Authorization") String accessToken,@RequestHeader("status") int status,@RequestHeader("startDate") String startDate, @RequestHeader("endDate") String endDate) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try{
            List<CycleResponseDTO> cycleResponseDTOList = multiService.getCycle(tokenDTO.username(),status, LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")), LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            return ResponseEntity.status(HttpStatus.OK).body(cycleResponseDTOList);
        }catch (NotAllowedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    //일정 태그리스트
    @GetMapping("/tag")
    public ResponseEntity<?> tagList(@RequestHeader("Authorization") String accessToken,@RequestHeader("status") int status) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) {
            List<CycleTagDTO> cycleTagDTOList = multiService.getTagList(tokenDTO.username(),status);
            return ResponseEntity.status(HttpStatus.OK).body(cycleTagDTOList);
        } else return tokenDTO.getResponseEntity();
    }

    //일정 태그삭제
    @DeleteMapping("/tag")
    public ResponseEntity<?> tagDelete(@RequestHeader("Authorization") String accessToken, @RequestHeader("id") Long id) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.deleteTag(id);
            return ResponseEntity.status(HttpStatus.OK).body("tagDelete");
        } catch (DataNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    //일정  태그수정
    @PutMapping("/tag")
    public ResponseEntity<?> tagUpdate(@RequestHeader("Authorization") String accessToken, @RequestHeader("id") Long id, @RequestBody CycleTagRequestDTO cycleTagRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            CycleTagDTO cycleTagDTO = multiService.updateTag(id, cycleTagRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body(cycleTagDTO);
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    //태그에 해당하는 일정리스트
    @GetMapping("/tagList")
    public ResponseEntity<?> tagCycleList(@RequestHeader("Authorization") String accessToken,@RequestHeader("id") Long id,@RequestHeader("page")int page){
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if(tokenDTO.isOK()){
            if (page < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 페이지를 찾을 수 없습니다.");
            }
            Page<CycleDTO> cycleDTOList = multiService.getTagCycle(id,page);
            return ResponseEntity.status(HttpStatus.OK).body(cycleDTOList);
        }else return tokenDTO.getResponseEntity();

    }
}
