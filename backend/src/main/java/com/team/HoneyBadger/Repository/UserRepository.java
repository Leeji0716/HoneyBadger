package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.SiteUser;
import com.team.HoneyBadger.Repository.Custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<SiteUser, String>, UserRepositoryCustom {

}
