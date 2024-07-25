package com.team.HoneyBadger.Repository.Custom;

import com.team.HoneyBadger.Entity.CycleTag;

import java.util.List;

public interface CycleTagRepositoryCustom {

    CycleTag findByName(String k, String name);

    List<CycleTag> myTag(String k);
}
