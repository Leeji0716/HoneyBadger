package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.PersonalCycleRequestDTO;
import com.team.HoneyBadger.Entity.PersonalCycle;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.PersonalCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalCycleService {
    private final PersonalCycleRepository personalCycleRepository;

    public void save(SiteUser user, PersonalCycleRequestDTO personalCycleRequestDTO) {
        personalCycleRepository.save(PersonalCycle.builder()
                .title(personalCycleRequestDTO.title())
                .content(personalCycleRequestDTO.content())
                .user(user)
                .startDate(personalCycleRequestDTO.startDate())
                .endDate(personalCycleRequestDTO.endDate())
                .build());
    }
}
