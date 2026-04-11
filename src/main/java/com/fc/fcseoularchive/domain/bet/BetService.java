package com.fc.fcseoularchive.domain.bet;

import com.fc.fcseoularchive.domain.bet.dto.*;
import com.fc.fcseoularchive.domain.game.Game;
import com.fc.fcseoularchive.domain.game.GameRepository;
import com.fc.fcseoularchive.domain.user.User;
import com.fc.fcseoularchive.domain.user.UserRepository;
import com.fc.fcseoularchive.global.error.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    //
    public BetResponse getBet(String loginId) {

        //LocalDateTime now = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.of(2026, 3, 17, 13, 0, 0); // 베팅 gameId=3 테스트용
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

        // 내가 베팅한 포인트 구하기
        BetHistory betHistory = betHistoryRepository.findByUser_IdAndGame_Id(loginId, game.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "<Database Error> 유저와 경기에 대한 bet_history가  존재하지 않습니다."));

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

        // from bet_history db
        response.setMyWinPoint(betHistory.getWinPoint());
        response.setMyDrawPoint(betHistory.getDrawPoint());
        response.setMyLosePoint(betHistory.getLosePoint());

        return response;
    }

    @Transactional
    public void createBet(String loginId, BetCreateRequest request) {

        //LocalDateTime now = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.of(2026, 3, 17, 13, 0, 0); // 베팅 gameId=3 테스트용
        //LocalDateTime now = LocalDateTime.of(2028, 3, 17, 13, 0, 0); // 베팅 경기 없는 테스트용

        // 애초에 베팅중인 경기가 없으면 베팅이 불가능
        Game game = gameRepository.findFirstByDateAfterOrderByDateAsc(now)
                .orElseThrow(() ->new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "베팅 진행중인 게임이 없으므로 베팅할 수 없습니다."));

        // 프론트에서 준 게임 id가 현재 베팅중인 게임이 아니면 throw
        if (!Objects.equals(game.getId(), request.getGameId())) {
            throw new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "요청된 게임 id와 진행중인 베팅id가 일치하지 않습니다..");
        }

        // loginId , gameId 로 된 bet_history 없으면 생성, 있으면 수정
        Optional<BetHistory> optBetHistory = betHistoryRepository.findByUser_IdAndGame_Id(loginId, game.getId());

        boolean isNewBettor = false; // 처음 해당 경기에 베팅 시작했는지
        String opponent = game.getHomeTeam().equals("FC서울") ? game.getAwayTeam() : game.getHomeTeam();
        User user = userRepository.findById(loginId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "유저를 찾을 수 없습니다."));

        // w,d,l 구하기
        long winPoint = request.getWinPoint();
        long drawPoint = request.getDrawPoint();
        long losePoint = request.getLosePoint();

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

    // todo N+1 해결 필요
    // 경기 1번 + (bet N번 + history N번) 호출
    public List<BetHistoryResponse> getBetHistory(String loginId) {
        // 현재 시간보다 앞서는 모든 경기를 최신순으로 가져오기

        //LocalDateTime now = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.of(2026, 3, 17, 13, 0, 0); // 베팅 gameId=3 테스트용
        //LocalDateTime now = LocalDateTime.of(2028, 3, 17, 13, 0, 0); // 베팅 경기 없는 테스트용

        // 경기 시각 < 현재 시각 만족 하는 모든 경기를 최신 순으로 가져오기
        List<Game> games = gameRepository.findByDateBeforeOrderByDateDesc(now);

        List<BetHistoryResponse> responses = new ArrayList<>();

        for (Game game: games) {
            Optional<Bet> optBet = betRepository.findByGame_Id(game.getId());
            // 경기에 대한 bet 테이블이 없으면 db 오류 -> throw
            if (optBet.isEmpty()) {
                throw new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "<Database Error> 경기에 대한 bet가 존재하지 않습니다.");
            }

            Bet bet = optBet.get();

            // 유저가 경기에 베팅 했는지
            Optional<BetHistory> optBetHistory = betHistoryRepository.findByUser_IdAndGame_Id(loginId, game.getId());
            if (optBetHistory.isEmpty()) {
                // 베팅 안했으면 continue
                continue;
            }
            BetHistory betHistory = optBetHistory.get();

            BetHistoryResponse response = new BetHistoryResponse();

            // 기본 정보
            response.setBetId(bet.getId());
            response.setBetHistoryId(betHistory.getId());
            response.setGameId(game.getId());

            String opponent = game.getHomeTeam().equals("FC서울") ? game.getAwayTeam() : game.getHomeTeam();
            response.setOpponent(opponent);
            response.setGameDate(game.getDate());
            response.setGameResult(game.getResult());

            // bet 집계 정보
            response.setTotalBettors(bet.getBettors());
            response.setTotalPoint(bet.getTotalPoint());
            response.setWinPoint(bet.getWinPoint());
            response.setDrawPoint(bet.getDrawPoint());
            response.setLosePoint(bet.getLosePoint());

            // 내 betHistory 정보
            response.setPayoutPoint(betHistory.getPayoutPoint());

            // 반환 리스트에 추가
            responses.add(response);

        }

        return responses;
    }

    // 정산 완료지만, 확인하지 않은 배팅 내역 반환
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
}
