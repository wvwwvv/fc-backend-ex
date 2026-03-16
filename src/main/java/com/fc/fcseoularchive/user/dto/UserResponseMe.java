package com.fc.fcseoularchive.user.dto;

import com.fc.fcseoularchive.domain.entity.User;
import com.fc.fcseoularchive.domain.enums.Role;
import lombok.Getter;

@Getter
public class UserResponseMe {


    private final Long id;

    private final String userId;

    private final String nickname;

    private final Role role;

    private final Integer points;

    private final Integer attendanceStreak;

    private final Integer checkPoint;



    public UserResponseMe(User user, Integer checkPoint ) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.points = user.getPoints();
        this.attendanceStreak = user.getAttendanceStreak();
        this.checkPoint = checkPoint;
    }

}
