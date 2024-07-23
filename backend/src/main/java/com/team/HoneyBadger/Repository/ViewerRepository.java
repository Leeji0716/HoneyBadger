package com.team.HoneyBadger.Repository;


import com.team.HoneyBadger.Entity.Viewer;
import com.team.HoneyBadger.Repository.Custom.ViewerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewerRepository extends JpaRepository<Viewer,Long>, ViewerRepositoryCustom {
}
