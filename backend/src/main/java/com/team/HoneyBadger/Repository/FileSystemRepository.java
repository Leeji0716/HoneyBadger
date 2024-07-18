package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.FileSystem;
import com.team.HoneyBadger.Repository.Custom.FileSystemCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileSystemRepository extends JpaRepository<FileSystem, String>, FileSystemCustom {
}
