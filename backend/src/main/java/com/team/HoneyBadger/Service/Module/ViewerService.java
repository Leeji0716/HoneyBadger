package com.team.HoneyBadger.Service.Module;

import com.team.HoneyBadger.Entity.Approval;
import com.team.HoneyBadger.Entity.Approver;
import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Entity.Viewer;
import com.team.HoneyBadger.Repository.ViewerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewerService {
    private final ViewerRepository viewerRepository;

    public Viewer save(SiteUser siteUser, Approval approval) {
        return viewerRepository.save (Viewer.builder ().user (siteUser).approval (approval).build ());
    }

    public Viewer get(SiteUser user, Approval approval) {
        return viewerRepository.findByUserAndApproval (user, approval);
    }

    public List<Viewer> getAll(Approval approval){
        return viewerRepository.findByApproval (approval);
    }

    public void delete(SiteUser user, Approval approval){
        Viewer viewer = viewerRepository.findByUserAndApproval (user,approval);
        viewerRepository.delete (viewer);
    }

}
