package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Entity.Viewer;
import com.team.HoneyBadger.Repository.ViewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewerService {
    private final ViewerRepository viewerRepository;

    public Viewer save(SiteUser siteUser, Approval approval) {
        return viewerRepository.save (Viewer.builder ().user (siteUser).approval (approval).build ());
    }
}
