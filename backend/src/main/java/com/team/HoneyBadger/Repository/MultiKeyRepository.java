package com.team.HoneyBadger.Repository;

import com.team.HoneyBadger.Entity.MultiKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MultiKeyRepository extends JpaRepository<MultiKey, String> {

}
