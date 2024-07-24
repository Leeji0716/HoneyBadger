package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Entity.SiteUser;

import java.util.List;

public interface ApprovalRepositoryCustom {
    List<Approval> findByUsername(String username);

}
