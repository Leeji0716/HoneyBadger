package com.team.HoneyBadger.DTO;

import com.team.HoneyBadger.Entity.PersonalCycle;
import lombok.Builder;

import java.util.List;
@Builder
public record PersonalCycleResponseDTO (List<PersonalCycle> personalCycles, boolean holiday){
}
