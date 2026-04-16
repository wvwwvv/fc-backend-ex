package com.fc.fcseoularchive.sangminTest;

import com.fc.fcseoularchive.domain.bet.Bet;
import com.fc.fcseoularchive.domain.bet.BetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
public class BetTest {

    @Autowired
    private BetRepository betRepository;

    @Test
    @Transactional(readOnly = true)
    @DisplayName("현재 베팅 게임 정보 반환")
    public void test1() throws Exception {
        List<Bet> bet = betRepository.findAll();
        for (Bet b : bet) {
            System.out.println(b.getTotalPoint());
        }
    }
}
