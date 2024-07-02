package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SiteUser, String> {
}
