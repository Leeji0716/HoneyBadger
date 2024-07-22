package com.team.HoneyBadger.DTO;

import com.team.HoneyBadger.Entity.Approver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Entity.Viewer;

import java.util.List;

public record ApprovalRequestDTO(String title, String content, String sender, List<String> approversname, List<String> viewersname ) {
}
