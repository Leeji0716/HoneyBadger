package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Entity.Viewer;

import java.util.List;

public interface ViewerRepositoryCustom {
    Viewer findByUserAndApproval(SiteUser user, Approval approval);
    List<Viewer> findByApproval(Approval approval);
}
