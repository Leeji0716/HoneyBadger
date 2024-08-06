package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Holiday;
import com.team.HoneyBadger.Repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;
    public Holiday getHoliday(LocalDate startDate){
        return holidayRepository.getHoliday(startDate);
    }
}
