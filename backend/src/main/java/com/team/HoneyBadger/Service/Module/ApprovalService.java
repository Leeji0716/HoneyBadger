package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.DTO.ApprovalRequestDTO;
import com.team.HoneyBadger.DTO.ApprovalResponseDTO;
import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Entity.Chatroom;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Enum.ApprovalStatus;
import com.team.HoneyBadger.Repository.ApprovalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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
                .status (ApprovalStatus.NOT_READ)
                .build());
    }


}
