package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.PersonalCycleRequestDTO;
import com.team.HoneyBadger.Entity.GroupCycle;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.GroupCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupCycleService {
    private final GroupCycleRepository groupCycleRepository;

    public void create(SiteUser user, PersonalCycleRequestDTO personalCycleRequestDTO) {
        groupCycleRepository.save(GroupCycle.builder()
                .title(personalCycleRequestDTO.title())
                .content(personalCycleRequestDTO.content())
                .startDate(personalCycleRequestDTO.startDate())
                .endDate(personalCycleRequestDTO.endDate())
                .group(user.getDepartment())
                .build());
    }
}
