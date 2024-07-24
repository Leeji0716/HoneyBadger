package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.CycleTag;

public interface CycleTagRepositoryCustom {

    CycleTag findByName(String k,String name);
}
