package com.fc.fcseoularchive.user.querydsl;

import com.fc.fcseoularchive.domain.entity.Seasonauth;
import com.fc.fcseoularchive.user.dto.UserResponse;

import java.util.List;

public interface UserRepositoryQuerydsl {

    List<Seasonauth> getUserAll();

}
