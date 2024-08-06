package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.DTO.TokenDTO;
import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/teamCycle")
public class TeamCycleController {
    private final MultiService multiService;

    @GetMapping
    public ResponseEntity<?> getTeamName(@RequestHeader("Authorization") String accessToken){
        TokenDTO tokenDTO = multiService.checkToken(accessToken);
        if(tokenDTO.isOK()){
            List<String> myTeam = multiService.findMyTeams(tokenDTO.username());
            return ResponseEntity.status(HttpStatus.OK).body(myTeam);
        }else return tokenDTO.getResponseEntity();
    }

}
