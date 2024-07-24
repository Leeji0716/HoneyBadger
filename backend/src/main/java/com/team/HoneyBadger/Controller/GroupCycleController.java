package com.team.HoneyBadger.Controller;

import com.team.HoneyBadger.Service.MultiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/GroupCycle")
public class GroupCycleController {
    private final MultiService multiService;


}
