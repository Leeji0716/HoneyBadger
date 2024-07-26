package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Enum.ApprovalStatus;
import com.team.HoneyBadger.Repository.ApproverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApproverService {
    private final ApproverRepository approverRepository;

    public Approver save(SiteUser siteUser, Approval approval){
        return approverRepository.save (Approver.builder ().user (siteUser).approval (approval).approverStatus (ApprovalStatus.READY).build ());
    }


    public Approver get(String username,Approval approval) {
        return approverRepository.findByUsernameAndApproval (username, approval);
    }

    public List<Approver> getAll(Approval approval){
        return approverRepository.findByApproval (approval);
    }

    @Transactional
    public void updateApproverStatus(Approval approval, String username, ApprovalStatus newStatus) {
        Approver approver = approverRepository.findByUsernameAndApproval (username,approval);
        if (approver != null) {
            approver.setApproverStatus (newStatus);
            approver.setCreateDate (LocalDateTime.now ());
            approverRepository.save (approver);
        }
    }


}
