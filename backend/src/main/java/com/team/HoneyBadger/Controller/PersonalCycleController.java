package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.*;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Exception.NotAllowedException;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
    public ResponseEntity<?> PersonalCycle(@RequestHeader("Authorization") String accessToken, @RequestBody PersonalCycleRequestDTO personalCycleRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.createPersonalCycle(tokenDTO.username(), personalCycleRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        } catch (NotAllowedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }

    //개인일정 수정
    @PutMapping
    public ResponseEntity<?> personalCycle(@RequestHeader("Authorization") String accessToken, @RequestHeader("id") Long id, @RequestBody PersonalCycleRequestDTO personalCycleRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if (tokenDTO.isOK()) try {
            multiService.updatePersonalCycle(tokenDTO.username(), id, personalCycleRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body("update");
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
            multiService.deletePersonalCycle(tokenDTO.username(), id);
            return ResponseEntity.status(HttpStatus.OK).body("delete");
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (NotAllowedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }


    //개인일정 리스트
    @GetMapping
    public ResponseEntity<?> myMonthCycle(@RequestHeader("Authorization") String accessToken, @RequestHeader("startDate") String startDate, @RequestHeader("endDate") String endDate){
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if(tokenDTO.isOK()) {
            List<PersonalCycleResponseDTO> personalCycleResponseDTOS = multiService.getMyCycle(tokenDTO.username(), LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")), LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
            return ResponseEntity.status(HttpStatus.OK).body(personalCycleResponseDTOS);
        }
        else return tokenDTO.getResponseEntity();
    }


    @PostMapping("/tag")
    public ResponseEntity<?> tagMake(@RequestHeader("Authorization") String accessToken, @RequestBody PersonalCycleRequestTagDTO personalCycleRequestTagDTO){
        TokenDTO tokenDTO =  multiService.checkToken(accessToken);
        if(tokenDTO.isOK())try{
            multiService.personalCycleTag(tokenDTO.username(), personalCycleRequestTagDTO.id(), personalCycleRequestTagDTO.tag());
            return ResponseEntity.status(HttpStatus.OK).body("ok!");
        }catch (NotAllowedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }
    @GetMapping("/tagList")
    public ResponseEntity<?> tagList(@RequestHeader("Authorization") String accessToken,@RequestHeader("tag") String tag){
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if(tokenDTO.isOK()){

         List<PersonalCycleDTO> personalCycleDTOList =  multiService.personalCycleTagList(tokenDTO.username(), URLDecoder.decode(tag, StandardCharsets.UTF_8));
         return ResponseEntity.status(HttpStatus.OK).body(personalCycleDTOList);
        }

        else return tokenDTO.getResponseEntity();
    }
}
