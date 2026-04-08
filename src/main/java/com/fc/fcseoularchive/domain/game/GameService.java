package com.fc.fcseoularchive.domain.game;

import com.fc.fcseoularchive.domain.post.Post;
import com.fc.fcseoularchive.global.error.ApiException;
import com.fc.fcseoularchive.domain.game.dto.GameAdminRequest;
import com.fc.fcseoularchive.domain.game.dto.GameResponse;
import com.fc.fcseoularchive.domain.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GameService {

    private final GameRepository gameRepository;
    private final PostRepository postRepository;


    // 경기 조회 (년, 월 필터링 param 은 nullable) - 로그인 유저만 가능
    public List<GameResponse> getAllGames(String loginId, Integer year, Integer month) {
        // 경기를 전부 가져올건데 -> 유저가 직관했다면 True 담아주고 아니라면, false 담아주는 로직

        // 경기 일단 다 가져옴 - getAll 메서드는 year, month 동적 쿼리로 null 처리 가능
        List<Game> gameAll = gameRepository.getAll(year, month);

        // post 가져올건데 이때 패치조인으로 경기랑, 유저까지 다 담아옴
        List<Post> postAll = postRepository.getPostAll(loginId);

        // game.id 가 키가되고, 그 안에 value 는 List<Post> 가 있다.
        Map<Long, List<Post>> postMap = postAll.stream()
                .collect(Collectors.groupingBy(post -> post.getGame().getId()));

        // Map<Long, List<Post>> postMap =
        // Long : game.id (1, 2, 3, 4, 5 ...)
        // List<Post> =  1 조회 -> <Post.game.id == 1 , ... . ._>

        // post -> 우리의 아이디가 담긴것만 가져왔어요. 이러면 1:1 매핑이됨, 대신 gameAll 에서 없는것도 있음. null 가능.
        return gameAll.stream()
                .map(
                        g -> {
                            List<Post> list = postMap.getOrDefault(g.getId(), List.of()).stream().toList();
                            boolean isAttended = false;

                            if(loginId != null){     // 로그인 했고,
                                if(!list.isEmpty()){ // 봤던 경기라면!
                                    isAttended = true;
                                }
                            }

                            // 상대팀 찾기 : 홈팀이 "FC서울" 이 아니면 awayTeam 이 opponent
                            String opponent = g.getHomeTeam().equals("FC서울") ? g.getAwayTeam() : g.getHomeTeam();
                            // 결과 : 결과가 있다면 FINISHED 없다면, SCHEDULED
                            String status = (g.getResult() == null ? "SCHEDULED" : "FINISHED");
                            return new GameResponse(g, opponent, status, isAttended);
                        }
                )
                .toList();
    }

    // 경기 조회 (년, 월 필터링) - 비로그인 가능
    @Cacheable(value = "guestGames", key = "#year + '-' + #month")
    public List<GameResponse> getAllGamesForGuest(Long loginId, Integer year, Integer month) {

        List<Game> gameAll = gameRepository.getAll(year, month);

        // post 만 가져옴 - guest 이므로 직관 여부 필요 없다
        List<Post> postAll = postRepository.findAll();;

        // game.id 가 키가되고, 그 안에 value 는 List<Post> 가 있다.
        Map<Long, List<Post>> postMap = postAll.stream()
                .collect(Collectors.groupingBy(post -> post.getGame().getId()));

        // Map<Long, List<Post>> postMap =
        // Long : game.id (1, 2, 3, 4, 5 ...)
        // List<Post> =  1 조회 -> <Post.game.id == 1 , ... . ._>

        // post -> 우리의 아이디가 담긴것만 가져왔어요. 이러면 1:1 매핑이됨, 대신 gameAll 에서 없는것도 있음. null 가능.
        return gameAll.stream()
                .map(
                        g -> {
                            List<Post> list = postMap.getOrDefault(g.getId(), List.of()).stream().toList();
                            boolean isAttended = false;

                            if(loginId != null){     // 로그인 했고,
                                if(!list.isEmpty()){ // 봤던 경기라면!
                                    isAttended = true;
                                }
                            }

                            // 상대팀 찾기 : 홈팀이 "FC서울" 이 아니면 awayTeam 이 opponent
                            String opponent = g.getHomeTeam().equals("FC서울") ? g.getAwayTeam() : g.getHomeTeam();
                            // 결과 : 결과가 있다면 FINISHED 없다면, SCHEDULED
                            String status = (g.getResult() == null ? "SCHEDULED" : "FINISHED");
                            return new GameResponse(g, opponent, status, isAttended);
                        }
                )
                .toList();
    }


    // id로 game 단건 조회
    public GameResponse getGameById(String loginId, Long gameId) {

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

        String opponent = game.getHomeTeam().equals("FC서울") ? game.getAwayTeam() : game.getHomeTeam();
        response.setOpponent(opponent);

        response.setStatus(game.getResult() == null ? "SCHEDULED" : "FINISHED");

        response.setResult(game.getResult() != null ? game.getResult() : null);

        return response;
    }


    // admin : 경기 추가
    @Transactional
    @CacheEvict(value = "guestGames", allEntries = true)
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
    @CacheEvict(value = "guestGames", allEntries = true)
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
    @CacheEvict(value = "guestGames", allEntries = true)
    public void deleteGame(Long gameId) {
        gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "경기가 존재하지 않습니다."));

        gameRepository.deleteById(gameId);
    }


}

