package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.PersonalCycleRequestDTO;
import com.team.HoneyBadger.Entity.PersonalCycle;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.PersonalCycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalCycleService {
    private final PersonalCycleRepository personalCycleRepository;

    public void create(SiteUser user, PersonalCycleRequestDTO personalCycleRequestDTO) {
        personalCycleRepository.save(PersonalCycle.builder()
                .title(personalCycleRequestDTO.title())
                .content(personalCycleRequestDTO.content())
                .user(user)
                .startDate(personalCycleRequestDTO.startDate())
                .endDate(personalCycleRequestDTO.endDate())
                .build());
    }

    public PersonalCycle upDate(PersonalCycle personalCycle, PersonalCycleRequestDTO personalCycleRequestDTO) {
        personalCycle.setTitle(personalCycleRequestDTO.title());
        personalCycle.setContent(personalCycleRequestDTO.content());
        personalCycle.setStartDate(personalCycleRequestDTO.startDate());
        personalCycle.setEndDate(personalCycleRequestDTO.endDate());
        return personalCycleRepository.save(personalCycle);
    }

    public PersonalCycle findById(Long id) throws DataNotFoundException {
        return personalCycleRepository.findById(id).orElseThrow(() -> new DataNotFoundException("PersonalCycle not found with id: " + id));
    }

    public void delete(PersonalCycle personalCycle) {
        personalCycleRepository.delete(personalCycle);
    }

    public List<PersonalCycle> myMonthCycle(SiteUser user, LocalDateTime startDate, LocalDateTime endDate){

        return personalCycleRepository.myMonthCycle(user,startDate,endDate);
    }


    public void setTag(PersonalCycle personalCycle, List<String> tag) {
        for(String tags : tag){
            personalCycle.getTag().add(tags);
        }
        personalCycleRepository.save(personalCycle);

    }

    public List<PersonalCycle> tagList(SiteUser user, String tag) {
        return personalCycleRepository.tagList(user,tag);
    }

    public void save(PersonalCycle personalCycle) {
        personalCycleRepository.save(personalCycle);
    }
}