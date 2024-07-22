package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Repository.Custom.ApprovalRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long>, ApprovalRepositoryCustom {
}
