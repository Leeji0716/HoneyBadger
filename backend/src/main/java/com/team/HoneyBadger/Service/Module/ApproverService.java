package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.*;
import com.team.HoneyBadger.Repository.ApproverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApproverService {
    private final ApproverRepository approverRepository;

    public Approver save(SiteUser siteUser, Approval approval){
        return approverRepository.save (Approver.builder ().user (siteUser).approval (approval).build ());
    }


}
