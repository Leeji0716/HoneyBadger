package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Enum.ApproverStatus;
import com.team.HoneyBadger.Repository.ApproverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApproverService {
    private final ApproverRepository approverRepository;

    public Approver save(SiteUser siteUser, Approval approval){
        return approverRepository.save (Approver.builder ().user (siteUser).approval (approval).approverStatus (ApproverStatus.NOT_READ).build ());
    }

    public Approver get(SiteUser user,Approval approval) {
        return approverRepository.findByUserAndApproval (user, approval);
    }

    public List<Approver> getAll(Approval approval){
        return approverRepository.findByApproval (approval);
    }

    @Transactional
    public void updateApproverStatus(Long approverId, ApproverStatus newStatus) {
        Approver approver = approverRepository.findById (approverId).orElse (null);
        if (approver != null) {
            approver.setApproverStatus (newStatus);
            approverRepository.save (approver);
        }
    }


}
