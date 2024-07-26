package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Entity.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApprovalRepositoryCustom {
    Page<Approval> findByUsername(String username, Pageable pageable);
    Page<Approval> findByUsernameAndKeyword(String username, String keyword, Pageable pageable);

}
