package com.fc.fcseoularchive.rank;


import com.fc.fcseoularchive.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankService {
        private final PostRepository postRepository;

        // ttl 설정 - 1분 년도 별로
        // attendance::2026 형태로 (value + key 조합) Redis 의 실제 Key 역할을 하며 서버에 객체 리스트 저장
        @Cacheable(value = "attendanceRank", key="#year")
        public List<AttendanceRankResponse> getAttendanceRank(int year) {
                // 0: 페이지 번호, 3: 페이지 크기
                // 앞에서 3개 객체만 가져오는 효과
                // n 명을 가져오고 싶으면 pageSize 를 n으로 수정
                List<AttendanceRankRow> rows = postRepository.findAttendanceTopKByYear(year, PageRequest.of(0,3));

                List<AttendanceRankResponse> result = new ArrayList<>();
                for (int i=0; i<rows.size(); i++) {
                        AttendanceRankRow row = rows.get(i); // i 번째 랭킹 유저 정보
                        result.add(new AttendanceRankResponse(
                                i+1, // 랭킹은 1부터 시작
                                row.getNickname(),
                                row.getCount()
                        ));
                }
                return result;
        }

        // ttl 설정 - 1분
        @Cacheable(value = "winRateRank", key= "#year")
        public List<WinRateRankResponse> getWinRateRank(int year) {
                // 3명에 대한 상위 승률 가져옴
                // 승률은 double 타입, 서비스 계층에서 소수점 처리 필요
                List<WinRateRankRow> rows = postRepository.findWinRateTopKByYear(year, PageRequest.of(0,3));

                List<WinRateRankResponse> result = new ArrayList<>();
                for (int i=0; i<rows.size(); i++) {
                        WinRateRankRow row = rows.get(i);

                        double rawWinRate = (double) row.getWinCount() / row.getTotalCount() * 100;
                        double winRate = Math.floor(rawWinRate * 10.0) / 10.0; // floor 는 소수점 다 날리기

                        result.add(new WinRateRankResponse(
                                i+1,
                                row.getNickname(),
                                winRate
                        ));
                }
                return result;
        }
}
