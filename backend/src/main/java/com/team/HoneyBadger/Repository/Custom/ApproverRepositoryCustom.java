package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Entity.Approver;
import com.team.HoneyBadger.Entity.SiteUser;

import java.util.List;

public interface ApproverRepositoryCustom {
    Approver findByUserAndApproval(SiteUser user, Approval approval);

    List<Approver> findByApproval(Approval approval);

    Approver findByUsernameAndApproval(String username, Approval approval);
}
