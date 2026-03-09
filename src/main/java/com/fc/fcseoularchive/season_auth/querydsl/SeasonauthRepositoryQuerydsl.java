package com.fc.fcseoularchive.season_auth.querydsl;

import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.season_auth.dto.SeasonResponse;

import java.util.List;

public interface SeasonauthRepositoryQuerydsl {

    List<Seasonauth> getSeasonList();


}
