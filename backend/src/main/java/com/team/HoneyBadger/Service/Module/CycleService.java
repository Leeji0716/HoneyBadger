package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.CycleRequestDTO;
import com.team.HoneyBadger.Entity.Cycle;
import com.team.HoneyBadger.Entity.CycleTag;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.CycleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CycleService {
    private final CycleRepository cycleRepository;

    public void create(String k, CycleRequestDTO cycleRequestDTO) {
        cycleRepository.save(Cycle.builder()
                .title(cycleRequestDTO.title())
                .content(cycleRequestDTO.content())
                .k(k)
                .startDate(cycleRequestDTO.startDate())
                .endDate(cycleRequestDTO.endDate())
                .tag(null)
                .build());
    }

    public Cycle upDate(Cycle cycle, CycleRequestDTO cycleRequestDTO) {
            cycle.setTitle(cycleRequestDTO.title());
            cycle.setContent(cycleRequestDTO.content());
            cycle.setStartDate(cycleRequestDTO.startDate());
            cycle.setEndDate(cycleRequestDTO.endDate());
            return cycleRepository.save(cycle);
    }
    public Cycle upDateAndDeleteTag(Cycle cycle, CycleRequestDTO cycleRequestDTO) {
        cycle.setTitle(cycleRequestDTO.title());
        cycle.setContent(cycleRequestDTO.content());
        cycle.setStartDate(cycleRequestDTO.startDate());
        cycle.setEndDate(cycleRequestDTO.endDate());
        cycle.setTag(null);
        return cycleRepository.save(cycle);
    }
    public Cycle upDateToTag(Cycle cycle, CycleRequestDTO cycleRequestDTO,CycleTag cycleTag) {
        cycle.setTitle(cycleRequestDTO.title());
        cycle.setContent(cycleRequestDTO.content());
        cycle.setStartDate(cycleRequestDTO.startDate());
        cycle.setEndDate(cycleRequestDTO.endDate());
        cycle.setTag(cycleTag);
        return cycleRepository.save(cycle);
    }

    public Cycle findById(Long id) throws DataNotFoundException {
        return cycleRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Cycle not found with id: " + id));
    }

    public void delete(Cycle cycle) {
        cycleRepository.delete(cycle);
    }

    public List<Cycle> myMonthCycle(String k, LocalDateTime startDate, LocalDateTime endDate) {

        return cycleRepository.myMonthCycle(k, startDate, endDate);
    }

    public void save(Cycle cycle) {
        cycleRepository.save(cycle);
    }

    public void createByTag(String k, String title, String content, LocalDateTime startDate, LocalDateTime endDate, CycleTag cycleTag) {
        cycleRepository.save(Cycle.builder()
                .title(title)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .k(k)
                .tag(cycleTag)
                .build());
    }

    public List<Cycle> findTagCycle(CycleTag cycleTag) {
        return cycleRepository.findTagCycle(cycleTag);
    }

    public Page<Cycle> findTagCycleToPaging(CycleTag cycleTag, Pageable pageable) {

        return cycleRepository.findTagCycleToPaging(cycleTag,pageable);
    }
}
