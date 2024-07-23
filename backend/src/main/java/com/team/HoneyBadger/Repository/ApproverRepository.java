package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Entity.Approver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.ApproverRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApproverRepository extends JpaRepository<Approver,Long>, ApproverRepositoryCustom {
}
