package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.ApprovalRequestDTO;
import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Entity.Viewer;
import com.team.HoneyBadger.Enum.ApprovalStatus;
import com.team.HoneyBadger.Exception.DataNotFoundException;
import com.team.HoneyBadger.Repository.ApprovalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopProxy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalRepository approvalRepository;

    @Transactional
    public Approval save(Approval approval) {
        return approvalRepository.save(approval);
    }


    @Transactional
    public Approval create(ApprovalRequestDTO approvalRequestDTO, SiteUser sender) {
        return approvalRepository.save(Approval.builder()
                .title (approvalRequestDTO.title ())
                .content (approvalRequestDTO.content ())
                .sender (sender)
                .status (ApprovalStatus.READY)
                .build());
    }

    public Approval updateStatus(Long approvalId, ApprovalStatus newStatus){
        Approval approval = approvalRepository.findById (approvalId).get ();
        approval.setStatus (newStatus);
        approvalRepository.save (approval);

        return approval;
    }

    public Approval get(Long approvalId){
        Approval approval = approvalRepository.findById (approvalId).orElseThrow (() -> new DataNotFoundException ("approval not found"));
        return approval;
    }

    public Approval addReader(Approval approval, String username){
        List<String> readUsers = approval.getReadUsers ();

        if(!readUsers.contains (username)){
            readUsers.add (username);
            approvalRepository.save (approval);
        }

        return approval;
    }

    public void delete(Approval approval){
        approvalRepository.delete (approval);
    }

    public Page<Approval> getList(String username, String keyword, Pageable pageable){
        if (keyword == null || keyword.isEmpty()) {
        return  approvalRepository.findByUsername (username, pageable);
        }
        else{
            return  approvalRepository.findByUsernameAndKeyword (username,keyword,pageable);
        }
    }


}
