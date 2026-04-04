package com.fc.fcseoularchive.domain.user.dto;

import com.fc.fcseoularchive.domain.user.User;
import com.fc.fcseoularchive.domain.user.Role;
import lombok.Getter;

@Getter
public class UserResponseMe {


    private final String id;

    private final String nickname;

    private final Role role;

    private final Integer points;

    private final Integer attendanceStreak;

    private final Integer checkPoint;



    public UserResponseMe(User user, Integer checkPoint ) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.points = user.getPoints();
        this.attendanceStreak = user.getAttendanceStreak();
        this.checkPoint = checkPoint;
    }

}
