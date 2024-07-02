package com.team.HoneyBadger.Repository;


import com.team.HoneyBadger.Entity.Auth;
import com.team.HoneyBadger.Entity.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    boolean existsByUser(SiteUser user);
    Optional<Auth> findByRefreshToken(String refreshToken);
}
