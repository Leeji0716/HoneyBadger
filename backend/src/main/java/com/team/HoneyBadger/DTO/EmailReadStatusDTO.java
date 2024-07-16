package com.team.HoneyBadger.DTO;

import lombok.Builder;

@Builder
public record EmailReadStatusDTO (String recipient, boolean isRead){
}
