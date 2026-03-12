
package com.fc.fcseoularchive.user.querydsl;

import com.fc.fcseoularchive.domain.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryQuerydsl {

    List<User> getUserAll();

    Optional<User> getUser(String userId);

}

