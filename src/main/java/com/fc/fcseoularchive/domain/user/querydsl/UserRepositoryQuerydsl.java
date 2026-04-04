
package com.fc.fcseoularchive.domain.user.querydsl;

import com.fc.fcseoularchive.domain.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryQuerydsl {

    List<User> getUserAll();

    Optional<User> getUser(String id);

}

