package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.MultiKey;
import com.team.HoneyBadger.Repository.MultiKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MultiKeyService {
    private final MultiKeyRepository multiKeyRepository;

    public MultiKey save(String k) {
        return save(k, new ArrayList<>());
    }

    public MultiKey save(String k, List<String> keyValues) {
        return multiKeyRepository.save(MultiKey.builder().k(k).keyValues(keyValues).build());
    }

    public Optional<MultiKey> get(String k) {
        return multiKeyRepository.findById(k);
    }

    public void updateOne(MultiKey key, String keyValue) {
        key.getKeyValues().add(keyValue);
        multiKeyRepository.save(key);
    }

    public void updateAll(MultiKey key, List<String> keyValues) {
        key.setKeyValues(keyValues);
        multiKeyRepository.save(key);
    }

    public void delete(MultiKey key) {
        multiKeyRepository.delete(key);
    }
}
