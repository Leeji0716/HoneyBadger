package com.team.HoneyBadger.Repository.Custom.Impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.HoneyBadger.Entity.FileSystem;
import com.team.HoneyBadger.Entity.QFileSystem;
import com.team.HoneyBadger.Repository.Custom.FileSystemCustom;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FileSystemCustomImpl implements FileSystemCustom {
    private final JPAQueryFactory jpaQueryFactory;

    QFileSystem qFileSystem = QFileSystem.fileSystem;

    public List<FileSystem> Files(Long emailId) {
        return jpaQueryFactory.selectFrom(qFileSystem).where(qFileSystem.k.startsWith("EMAIL_RESERVATION_" + emailId)).fetch();
    }
}
