package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.FileSystem;
import com.team.HoneyBadger.Repository.FileSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileSystemService {
    private final FileSystemRepository fileSystemRepository;

    public FileSystem save(String k, String v) {
        return fileSystemRepository.save(FileSystem.builder().k(k).v(v).build());
    }

    public Optional<FileSystem> get(String k) {
        return fileSystemRepository.findById(k);
    }

    public void deleteByKey(FileSystem fileSystem) {
        fileSystemRepository.delete(fileSystem);
    }
}
