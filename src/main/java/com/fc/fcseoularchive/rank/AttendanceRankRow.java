package com.fc.fcseoularchive.rank;


// Projection 에 사용되는 interface
public interface AttendanceRankRow {
    String getNickname();
    Long getCount(); // 직관 경기 횟수
}
