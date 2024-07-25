package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.CycleTagRequestDTO;
import com.team.HoneyBadger.Entity.CycleTag;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.CycleTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CycleTagService {
    private final CycleTagRepository cycleTagRepository;

    public CycleTag findByName(String k,String name) {
        return cycleTagRepository.findByName(k,name);
    }

    public CycleTag create(String k, String name, String color) {
        return cycleTagRepository.save(CycleTag.builder().name(name)
                .color(color)
                .k(k)
                .build());
    }

    public void delete(CycleTag cycleTag) {
        cycleTagRepository.delete(cycleTag);
    }

    public List<CycleTag> myTag(String k) {
        return cycleTagRepository.myTag(k);
    }

    public CycleTag updateTag(CycleTag cycleTag, CycleTagRequestDTO cycleTagRequestDTO) {
        cycleTag.setName(cycleTagRequestDTO.name());
        cycleTag.setColor(cycleTagRequestDTO.color());
        return cycleTagRepository.save(cycleTag);
    }

    public CycleTag findById(Long id) {
        return cycleTagRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Not Found"));
    }
}
