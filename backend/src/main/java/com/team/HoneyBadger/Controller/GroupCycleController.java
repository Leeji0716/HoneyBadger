package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.PersonalCycleRequestDTO;
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
@RequestMapping("/api/GroupCycle")
public class GroupCycleController {
    private final MultiService multiService;

    //그룹일정 생성
    @PostMapping
    public ResponseEntity<?> createGroupCycle(@RequestHeader("Authorization") String accessToken, @RequestBody PersonalCycleRequestDTO personalCycleRequestDTO) {
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if(tokenDTO.isOK())try{
            multiService.createGroupCycle(tokenDTO.username(),personalCycleRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        }catch (NotAllowedException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
   }

   //그룹일정 수정
//    @PutMapping
//    public ResponseEntity<?> updateGroupCycle(@RequestHeader("Authorization") String accessToken,@RequestHeader("id") Long id, @RequestBody PersonalCycleRequestDTO personalCycleRequestDTO){
//        TokenDTO tokenDTO = multiService.checkToken(accessToken);
//        if(tokenDTO.isOK())try {
//
//        }catch (){
//
//        }
//    }


}
