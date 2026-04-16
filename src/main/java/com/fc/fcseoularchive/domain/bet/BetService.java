package com.fc.fcseoularchive.domain.bet;

import com.fc.fcseoularchive.domain.bet.dto.*;
import com.fc.fcseoularchive.domain.game.Game;
import com.fc.fcseoularchive.domain.game.GameRepository;
import com.fc.fcseoularchive.domain.game.GameResult;
import com.fc.fcseoularchive.domain.user.User;
import com.fc.fcseoularchive.domain.user.UserRepository;
import com.fc.fcseoularchive.global.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BetService {

    private final BetRepository betRepository;
    private final BetHistoryRepository betHistoryRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "betSummary", key = "#loginId")
    public BetSummaryResponse getBetSummary(String loginId) {
        // loginId로 bet_history 에서 찾기

        List<BetHistory> betHistoryList = betHistoryRepository.findAllByUser_Id(loginId);

        BetSummaryResponse response = new BetSummaryResponse();

        long totalNumber = 0L; // 본인이 베팅에 참여한 경기 수
        long gain = 0L;
        long loss = 0L;

        for (BetHistory history : betHistoryList) {
            long benefit = history.getPayoutPoint();

            totalNumber++;
            if (benefit > 0) gain += benefit;
            else loss += benefit * -1;
        }

        response.setUserId(loginId);
        response.setTotalNumber(totalNumber);
        response.setGain(gain);
        response.setLoss(loss);

        return response;

    }

    // 현재 베팅중인 경기 정보
    @Cacheable(value = "betGame", key = "#loginId")
    public BetResponse getBet(String loginId) {

        LocalDateTime now = LocalDateTime.now();
        //LocalDateTime now = LocalDateTime.of(2026, 3, 17, 13, 0, 0); // 베팅 gameId=3 테스트용
        //LocalDateTime now = LocalDateTime.of(2028, 3, 17, 13, 0, 0); // 베팅 경기 없는 테스트용

        BetResponse response = new BetResponse();

        response.setUserId(loginId);

        // 현재 시각 -> 현재 베팅 중인 경기 구하기
        // 현재 시각 < 경기 시각  만족 하는 경기 중, 가장 날짜가 작은 경기 구하기 : 가장 가까운 미래의 경기
        Game game = gameRepository.findFirstByDateAfterOrderByDateAsc(now)
                .orElse(null);

        // bet 중인 경기가 없으면 유저 아이디만 반환, 나머지는 null 이나 기본값
        if (game == null) return response;

        // gameId가 있으니 bet 에서 포인트 정보, games 에서 opponent, date 가져오기
        Bet bet = betRepository.findByGame_Id(game.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "<Database Error> 경기에 대한 bet가 존재하지 않습니다."));

        response.setBetId(bet.getId());
        response.setGameId(game.getId());

        // from game db
        response.setGameDate(game.getDate());
        String opponent = game.getHomeTeam().equals("FC서울") ? game.getAwayTeam() : game.getHomeTeam();
        response.setOpponent(opponent);

        // from bet db
        response.setTotalBettors(bet.getBettors());
        response.setTotalPoint(bet.getTotalPoint());
        response.setWinPoint(bet.getWinPoint());
        response.setDrawPoint(bet.getDrawPoint());
        response.setLosePoint(bet.getLosePoint());

        Optional<BetHistory> optBetHistory = betHistoryRepository.findByUser_IdAndGame_Id(loginId, game.getId());
        if (optBetHistory.isPresent()) {
            BetHistory betHistory = optBetHistory.get();
            response.setMyWinPoint(betHistory.getWinPoint());
            response.setMyDrawPoint(betHistory.getDrawPoint());
            response.setMyLosePoint(betHistory.getLosePoint());
        } else {
            response.setMyWinPoint(0L);
            response.setMyDrawPoint(0L);
            response.setMyLosePoint(0L);
        }

        return response;
    }

    // 베팅 하기 - bet, betHistory 수정 필요
    @Transactional
    @CacheEvict(value = "betGame", allEntries = true)
    public void createBet(String loginId, BetCreateRequest request) {

        LocalDateTime now = LocalDateTime.now();
        //LocalDateTime now = LocalDateTime.of(2026, 3, 17, 13, 0, 0); // 베팅 gameId=3 테스트용
        //LocalDateTime now = LocalDateTime.of(2028, 3, 17, 13, 0, 0); // 베팅 경기 없는 테스트용

        // 애초에 베팅중인 경기가 없으면 베팅이 불가능
        Game game = gameRepository.findFirstByDateAfterOrderByDateAsc(now)
                .orElseThrow(() ->new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "베팅 진행중인 게임이 없으므로 베팅할 수 없습니다."));

        // 프론트에서 준 게임 id가 현재 베팅중인 게임이 아니면 throw
        if (!Objects.equals(game.getId(), request.getGameId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "요청된 게임 id와 진행중인 베팅id가 일치하지 않습니다..");
        }

        boolean isNewBettor = false; // 처음 해당 경기에 베팅 시작했는지
        String opponent = game.getHomeTeam().equals("FC서울") ? game.getAwayTeam() : game.getHomeTeam();

        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "유저를 찾을 수 없습니다."));

        // w,d,l 구하기
        long winPoint = request.getWinPoint();
        long drawPoint = request.getDrawPoint();
        long losePoint = request.getLosePoint();
        long sum = winPoint + drawPoint + losePoint;

        // 한번에 한 결과만 베팅 가능, 음수 베팅 불가능
        if (!validPointRequest(winPoint, drawPoint, losePoint)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "올바른 베팅 형식이 아닙니다.");

        }

        if (sum > user.getPoints()) {
            // 포인트 보다 크게 베팅
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "잔액이 부족합니다.");
        } else {
            // 베팅 하는 순간 포인트 차감
            user.subtractPoints((int)sum);
        }

        // loginId , gameId 로 된 bet_history 없으면 생성, 있으면 수정
        Optional<BetHistory> optBetHistory = betHistoryRepository.findByUser_IdAndGame_Id(loginId, game.getId());

        if (optBetHistory.isEmpty()) {
            // 새로 betHistory 만들기
            BetHistory betHistory = new BetHistory(user, game, game.getDate(), opponent);
            betHistory.addPoints(winPoint, drawPoint, losePoint);

            betHistoryRepository.save(betHistory);
            isNewBettor = true;
        } else {
            // 이미 존재하는 row 업데이트
            optBetHistory.get().addPoints(winPoint, drawPoint, losePoint);
        }

        // bet_history 생성했으면 addBetting 마지막 param 에 true 주고 bet 업데이트
        Bet bet = betRepository.findByGame_Id(game.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "<Database Error> 경기에 대한 bet가 존재하지 않습니다."));

        bet.addBetting(winPoint, drawPoint, losePoint, isNewBettor);
    }

    // point request 유효성 검사
    private boolean validPointRequest(long winPoint, long drawPoint, long losePoint) {
        boolean result = true;

        int numOfZero = 0;

        if (winPoint == 0) numOfZero++;
        if (drawPoint == 0) numOfZero++;
        if (losePoint == 0) numOfZero++;

        // w, d, l 중 1개만 베팅 가능, 나머지 request 값은 0이어야 한다
        if (numOfZero != 2) result = false;

        // 음수 베팅 불가능
        if (winPoint<0 || drawPoint<0 || losePoint<0) result = false;

        return result;
    }

    // 과거 경기 기준 내 베팅 이력 조회
    // DTO projection
    @Cacheable(value = "betHistory", key = "#loginId")
    public List<BetHistoryResponse> getBetHistory(String loginId) {
        LocalDateTime now = LocalDateTime.now();
        //LocalDateTime now = LocalDateTime.of(2026, 3, 17, 13, 0, 0); // 베팅 gameId=3 테스트용
        //LocalDateTime now = LocalDateTime.of(2028, 3, 17, 13, 0, 0); // 베팅 경기 없는 테스트용

        return betHistoryRepository.getBetHistory(loginId, now);
    }

    // 정산 완료했지만, 확인하지 않은 배팅 내역 반환
    // DTO projection
    public List<UnreadBetResultResponse> getUnreadBetResult(String loginId) {
        return betHistoryRepository.getUnreadBetResults(loginId);
    }

    // isChecked -> true
    @Transactional
    public void checkUnreadBetResult(String loginId, BetHistoryIdsRequest request) {

        // loginId, betHistoryIds 조합으로 된 betHistory 의 isChecked 를 true 로 수정
        for (Long betHistoryId : request.getBetHistoryIds()) {
            BetHistory betHistory = betHistoryRepository.findById(betHistoryId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "betHistory를 찾을 수 없습니다."));


            // 본인 데이터만 처리 가능
            if (!betHistory.getUser().getId().equals(loginId)) {
                throw new ApiException(HttpStatus.FORBIDDEN, "403", "FORBIDDEN", "본인의 betHistory만 확인 처리할 수 있습니다. betHistoryId=" + betHistoryId);
            }

            // 정산 완료 건만 확인 처리 가능
            if (!betHistory.isSettled()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "정산되지 않은 betHistory는 확인 처리할 수 없습니다. betHistoryId=" + betHistoryId);
            }

            // 미확인 상태일 때만 체크
            if (!betHistory.isChecked()) {
                betHistory.markAsChecked();
            }
        }
    }

    // 베팅 정산
    @Transactional
    public void settleAllBet(Long gameId) {

        // betHistory : gameId 로 전체 조회, user fetch join
        List<BetHistory> betHistoryList = betHistoryRepository.findAllByGame_IdAndFetchUser(gameId);
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "경기를 찾을 수 없습니다."));


        // w, d, l 여부에 따라 payout_point 갱신
        GameResult gameResult = game.getResult();

        for (BetHistory betHistory : betHistoryList) {

            if (betHistory.isSettled()) {
                // 이미 정산 되었으면 throw
                throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "이미 정산된 경기입니다. betHistoryId=" + betHistory.getId());
            }

            Long winPoint = betHistory.getWinPoint();
            Long drawPoint = betHistory.getDrawPoint();
            Long losePoint = betHistory.getLosePoint();
            long payoutPoint = 0L; // 유저의 이득 포인트

            User user = betHistory.getUser(); // fetch join 으로 이미 로딩된 user

            if (gameResult.equals(GameResult.W)) {
                payoutPoint = 2*winPoint - drawPoint - losePoint;

                // w 인 경우 win_point * 2 만큼 유저에게 정산
                if (winPoint != 0) {
                    user.addPoints((int) (2*winPoint));
                }
            } else if (gameResult.equals(GameResult.D)) {
                payoutPoint = 2*drawPoint - winPoint - losePoint;

                if (drawPoint != 0) {
                    user.addPoints((int) (2*drawPoint));
                }
            } else if (gameResult.equals((GameResult.L))) {
                payoutPoint = 2*losePoint - winPoint - drawPoint;

                if (losePoint != 0) {
                    user.addPoints((int) (2*losePoint));
                }
            }

            // payoutPoint 갱신
            // isSettled = true, isChecked = false
            betHistory.settle(payoutPoint);
        }
    }
}
