package com.fc.fcseoularchive.game;
import com.fc.fcseoularchive.error.ApiException;
import com.fc.fcseoularchive.domain.entity.Game;
import com.fc.fcseoularchive.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;
    private final PostRepository postRepository;

    // 경기 조회 (년, 월 필터링)
    public List<GameResponse> getAllGames(Integer year, Integer month) {
        List<Game> games;
        if (year != null && month != null) {
            games = gameRepository.findByYearOrderByDateAsc(year, month);
        } else if (year == null && month == null) {
            games = gameRepository.findAllByOrderByDateAsc();
        } else {
            throw new ApiException(HttpStatus.BAD_REQUEST, "400", "BAD_REQUEST", "year 와 month 값이 필요합니다.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdByString = authentication.getName();
        Long loginId = Long.parseLong(userIdByString); // 로그인 유저의 id

        return games.stream().map(game -> {
            GameResponse response = new GameResponse();

            response.setId(game.getId());
            response.setDate(game.getDate());
            response.setRound(game.getRound());
            response.setHomeTeam(game.getHomeTeam());
            response.setAwayTeam(game.getAwayTeam());
            response.setStadium(game.getStadium());
            response.setHomeScore(game.getHomeScore());
            response.setAwayScore(game.getAwayScore());

            // "isAttended" : true - 로그인 user 가 간 경기인지
            // post db 의 game_id == game.getId 인 행에서 user_id == loginId 인지 판단
            boolean existPost = postRepository.existsByUserIdAndGameId(loginId, game.getId());
            response.setIsAttended(existPost);

            // 상대팀 찾기 : 홈팀이 "FC Seoul" 이 아니면 awayTeam 이 opponent
            String opponent = game.getHomeTeam().equals("FC Seoul") ? game.getAwayTeam() : game.getHomeTeam();
            response.setOpponent(opponent);

            // 경기 결과 (W, D, L) 가 null 이면 "경기 전"
            response.setStatus(game.getResult() == null ? "SCHEDULED" : "FINISHED");

            // 경기 결과가 null 이 아니면 String 으로 변환 해서 반환
            response.setResult(game.getResult() != null ? game.getResult().toString() : null);

            return response;
        }).collect(Collectors.toList());
    }


    // Guest용 특정 연도 경기 정보 조회 // todo ttl 1시간
    @Cacheable(value = "guestGamesByYear", key = "#year")
    public List<GameResponse> getAllGamesForGuestByYear(int year, int month) {
        List<Game> games = gameRepository.findByYearOrderByDateAsc(year, month);

        return games.stream().map(game -> {
            GameResponse response = new GameResponse();

            response.setId(game.getId());
            response.setDate(game.getDate());
            response.setRound(game.getRound());
            response.setHomeTeam(game.getHomeTeam());
            response.setAwayTeam(game.getAwayTeam());
            response.setStadium(game.getStadium());
            response.setHomeScore(game.getHomeScore());
            response.setAwayScore(game.getAwayScore());

            response.setIsAttended(false);

            // 상대팀 찾기 : 홈팀이 "FC Seoul" 이 아니면 awayTeam 이 opponent
            String opponent = game.getHomeTeam().equals("FC Seoul") ? game.getAwayTeam() : game.getHomeTeam();
            response.setOpponent(opponent);

            // 경기 결과 (W, D, L) 가 null 이면 "경기 전"
            response.setStatus(game.getResult() == null ? "SCHEDULED" : "FINISHED");

            // 경기 결과가 null 이 아니면 String 으로 변환 해서 반환
            response.setResult(game.getResult() != null ? game.getResult().toString() : null);

            return response;
        }).collect(Collectors.toList());
    }

    public GameResponse getGameByUser(Long gameId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdByString = authentication.getName();
        Long loginId = Long.parseLong(userIdByString); // 로그인 유저의 id

        Game game = gameRepository.findById(gameId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않는 경기입니다."));

        GameResponse response = new GameResponse();

        response.setId(game.getId());
        response.setDate(game.getDate());
        response.setRound(game.getRound());
        response.setHomeTeam(game.getHomeTeam());
        response.setAwayTeam(game.getAwayTeam());
        response.setStadium(game.getStadium());
        response.setHomeScore(game.getHomeScore());
        response.setAwayScore(game.getAwayScore());

        boolean existPost = postRepository.existsByUserIdAndGameId(loginId, game.getId());
        response.setIsAttended(existPost);

        String opponent = game.getHomeTeam().equals("FC Seoul") ? game.getAwayTeam() : game.getHomeTeam();
        response.setOpponent(opponent);

        response.setStatus(game.getResult() == null ? "SCHEDULED" : "FINISHED");

        response.setResult(game.getResult() != null ? game.getResult().toString() : null);

        return response;
    }



    // admin : 경기 추가
    @Transactional
    public void addGame(GameAdminRequest request) {
        Game game = Game.builder()
                .date(request.getDate())
                .stadium(request.getStadium())
                .round(request.getRound())
                .homeTeam(request.getHomeTeam())
                .awayTeam(request.getAwayTeam())
                .homeScore(request.getHomeScore())
                .awayScore(request.getAwayScore())
                .result(request.getResult())
                .deletedAt(request.getDeletedAt())
                .build();

        // created_at, updated_at 은 onCreated 로 자동 적용
        gameRepository.save(game);
    }

    // admin : 경기 정보 1개 가져 오기
    public Game getGame(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않는 경기입니다."));

    }

    // admin : 경기 1개 정보 수정 (모든 필드 제어 가능)
    @Transactional
    public Game updateGame(Long gameId, GameAdminRequest request) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않는 경기입니다."));

        // 모든 필드 업데이트
        game.adminUpdate(
                request.getDate(),
                request.getStadium(),
                request.getRound(),
                request.getHomeTeam(),
                request.getAwayTeam(),
                request.getHomeScore(),
                request.getAwayScore(),
                request.getResult(),
                request.getDeletedAt()
        );

        return gameRepository.save(game);
    }

    // admin : 경기 삭제
    @Transactional
    public void deleteGame(Long gameId) {
        gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "경기가 존재하지 않습니다."));

        gameRepository.deleteById(gameId);
    }


}

