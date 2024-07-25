package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.CycleTag;
import com.team.HoneyBadger.Repository.CycleTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
