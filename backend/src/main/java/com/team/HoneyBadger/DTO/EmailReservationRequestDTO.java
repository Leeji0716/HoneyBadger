package com.team.HoneyBadger.DTO;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public record EmailReservationRequestDTO(String title, String content, String senderId, List<String> receiverIds,
                                        LocalDateTime sendTime, List<MultipartFile> attachments) {
}
