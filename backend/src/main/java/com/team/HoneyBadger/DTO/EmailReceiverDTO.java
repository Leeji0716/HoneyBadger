package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record EmailReceiverDTO(String receiverUsername, boolean status) {
}
