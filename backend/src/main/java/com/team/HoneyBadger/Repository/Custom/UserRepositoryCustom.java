package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {
    List<SiteUser> getUsersDepartmentIsNull();
    Page<SiteUser> getUsers(String keyword, Pageable pageable);
}
