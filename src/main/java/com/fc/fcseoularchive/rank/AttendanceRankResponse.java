package com.fc.fcseoularchive.rank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 필요 : Redis 에 저장된 JSON -> AttendanceRankResponse 에 빈 객체 필요하기 때문
@AllArgsConstructor
public class AttendanceRankResponse {

    private int rank;
    private String nickname;
    private Long count;


}
