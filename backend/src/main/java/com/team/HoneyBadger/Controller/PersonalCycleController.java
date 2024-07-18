package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.PersonalCycleRequestDTO;
import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Personal")
public class PersonalCycleController {

    private final MultiService multiService;


    @PostMapping
    public ResponseEntity<?> PersonalCycle(@RequestHeader("Authorization") String accessToken, @RequestBody PersonalCycleRequestDTO personalCycleRequestDTO){
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if(tokenDTO.isOK()) try {
            multiService.createPersonalCycle(tokenDTO.username(),personalCycleRequestDTO);
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        }catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        else return tokenDTO.getResponseEntity();
    }
}
