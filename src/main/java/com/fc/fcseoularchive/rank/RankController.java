package com.fc.fcseoularchive.rank;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "6. RankController", description = "랭킹 API")
@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankController {
    private final RankService rankService;

    @Operation(summary = "시즌(year)별 직관왕 랭킹")
    @GetMapping("/attendance")
    public List<AttendanceRankResponse> getAttendanceRank(@RequestParam int year) {
        return rankService.getAttendanceRank(year);
    }

    @Operation(summary = "시즌(year)별 승률왕 랭킹")
    @GetMapping("/win-rate")
    public List<WinRateRankResponse> getWinRateRank(@RequestParam int year) {
        return rankService.getWinRateRank(year);
    }
}
