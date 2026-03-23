package com.fc.fcseoularchive.player;

import com.fc.fcseoularchive.domain.entity.Donation;
import com.fc.fcseoularchive.domain.entity.Player;
import com.fc.fcseoularchive.domain.enums.PlayerPosition;
import com.fc.fcseoularchive.domain.enums.PlayerStatus;
import com.fc.fcseoularchive.donation.DonationRepository;
import com.fc.fcseoularchive.error.ApiException;
import com.fc.fcseoularchive.player.dto.CreatePlayerRequest;
import com.fc.fcseoularchive.player.dto.PlayerResponse;
import com.fc.fcseoularchive.player.dto.PlayerResponseRank;
import com.fc.fcseoularchive.player.dto.UpdatePlayerReqeust;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final DonationRepository donationRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/upload/player";

    // 선수 생성
    @Transactional
    @CacheEvict(value = "allPlayers", allEntries = true)
    public void createPlayer(CreatePlayerRequest req) throws IOException {

        // 업로드 폴더 없으면 폴더 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 이미지가 null이 아니고, 비어있지 않다면
        String imagePath = null;
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + req.getImage().getOriginalFilename(); // UUID_파일 이름으로 파일 이름 지정해주기
            File dest = new File(uploadDir, fileName); // 경로/fileName 으로 파일 만듦
            req.getImage().transferTo(dest); // 실제 파일 저장해두기
            imagePath = "/upload/player/" + fileName; // 이렇게 해야 db에 저장을 상대경로로 저장해서 프론트에서 바로 사용 가능함 !
        }

        // 객체 생성
        Player player = new Player(req, imagePath);
        playerRepository.save(player);
    }


    // 선수 전체 조회 (현역, 임대, 은퇴)
    public List<PlayerResponse> getAllPlayersV1() {
        return playerRepository.findAll()
                .stream()
                .map(p -> new PlayerResponse(p))
                .toList();
    }

    // 선수 단건 조회
    public PlayerResponse getPlayer(long id) {
        return new PlayerResponse(playerRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 선수입니다.")));
    }

    // 선수 업데이트
    @Transactional
    @CacheEvict(value = "allPlayers", allEntries = true)
    public void updatePlayer(long id, UpdatePlayerReqeust req) throws IOException {
        // 정보 가져오기
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 선수입니다."));

        // 등번호 변경 해야한다면,
        if (req.getBackNumber() != null) {
            player.updateBackNumber(req.getBackNumber());
        }

        // 상태 변경 해야한다면, (현역,임대,은퇴)
        if (req.getStatus() != null) {
            player.updateStatus(req.getStatus());
        }

        // 포지션 변경 해야한다면,
        if (req.getPosition() != null) {
            player.updatePosition(req.getPosition());
        }

        // 업로드 폴더 없으면 폴더 생성 (그럴 일은 없지만.. 데이터 날리는 바람에 넣어줌)
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 이미지 변경 해야한다면,
        if (req.getImage() != null && !req.getImage().isEmpty()) {
            String fileName = UUID.randomUUID() + "_" + req.getImage().getOriginalFilename(); // UUID_파일 이름으로 파일 이름 지정해주기
            File dest = new File(uploadDir, fileName); // 경로/fileName 으로 파일 만듦
            req.getImage().transferTo(dest); // 실제 파일 저장해두기
            player.updateImage("/upload/player/" + fileName);
        }

        // 변경 감지로 저장 따로 안해줘도 수정 완료! (더티 체킹)
    }

    // 현역선수 전체 조회
    public List<PlayerResponse> getAllActivePlayers() {
        return playerRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() == PlayerStatus.ACTIVE)
                .map(p -> new PlayerResponse(p))
                .toList();
    }


    /**
     * 랭킹 관련
     * getAllPlayersV2 (현역 선수 전체 조회 + 후원 랭킹)
     * getAllPlayersFW (현역 FW 선수 전체 조회 + 후원 랭킹)
     * getAllPlayersMF (현역 MF 선수 전체 조회 + 후원 랭킹)
     * getAllPlayersDF (현역 DF 선수 전체 조회 + 후원 랭킹)
     * getAllPlayersGK (현역 GK 선수 전체 조회 + 후원 랭킹)
     */

    // donation 에 nickname 추가 - 반정규화
    // 선수 전체 조회 + 유저 랭킹
    // cacheEvict : 1. 선수에게 후원할 때, 2. 선수 정보가 수정(삭제)될 때
    @Cacheable(value = "allPlayers", key = "'2026'")
    public List<PlayerResponseRank> getAllPlayersV2() {

        // 1. player 전부 가져오기 (현역 선수만!)
        List<Player> players = playerRepository.findByStatus(PlayerStatus.ACTIVE);

        // 2. donation <-> 기준 player 모두 가져오기
        List<Donation> donations = donationRepository.getDonationAll();

        // 3. player의 id 를 기준으로 그루핑 하기
        // -> 처음 접해보는 stream().collect(Collectors.groupingBy());
        // d.getPlayer().getId() 를 기준(Key) 으로 같은 Key 를 Donation 끼리 묶어서 그룹핑(리스트로) 하기
        Map<Integer, List<Donation>> donationMap = donations.stream()
                .collect(Collectors.groupingBy(d -> d.getPlayer().getId()));

        // 4. players 를 stream 돌면서 매핑하기 (이때 TOP3 뽑으면 됨)
        return players.stream()
                .map(
                        p -> {
                            // 5. 3번 맵 기준으로 가져오는데, 비어있으면 빈 배열로 반환
                            List<Donation> donationList = limitDonation(p, donationMap);

                            // 6. for문을 통해서 Map 형태에 채워주기
                            HashMap<Integer, String> nicknameList = new HashMap<>();
                            RankNicknameList(donationList, nicknameList);
                            return new PlayerResponseRank(p, nicknameList);
                        })
                .toList();
    }

    public List<PlayerResponseRank> getAllPlayersFW() {
        // 포지션 FW인 선수만 뽑기
        List<Player> players = playerRepository.findByStatusAndPosition(PlayerStatus.ACTIVE, PlayerPosition.FW);

        List<Donation> donations = donationRepository.getDonationAllFW(); // 여기는 모둔 포지션 선수 다 들어있음 맞지? 그렇다면 FW 선수만 가져와야함.

        Map<Integer, List<Donation>> donationMap = donations.stream()
                .collect(Collectors.groupingBy(d -> d.getPlayer().getId()));

        return players.stream()
                .map(p -> {
                    List<Donation> donationList = limitDonation(p, donationMap);
                    HashMap<Integer, String> nicknameList = new HashMap<>();
                    RankNicknameList(donationList, nicknameList);
                    return new PlayerResponseRank(p, nicknameList);
                })
                .toList();
    }

    public List<PlayerResponseRank> getAllPlayersMF() {
        // 포지션 MF인 선수만 뽑기
        List<Player> players = playerRepository.findByStatusAndPosition(PlayerStatus.ACTIVE, PlayerPosition.MF);

        List<Donation> donations = donationRepository.getDonationAllMF();

        Map<Integer, List<Donation>> donationMap = donations.stream()
                .collect(Collectors.groupingBy(d -> d.getPlayer().getId()));

        return players.stream()
                .map(p -> {
                    List<Donation> donationList = limitDonation(p, donationMap);
                    HashMap<Integer, String> nicknameList = new HashMap<>();
                    RankNicknameList(donationList, nicknameList);
                    return new PlayerResponseRank(p, nicknameList);
                })
                .toList();
    }

    public List<PlayerResponseRank> getAllPlayersDF() {
        // 포지션 DF인 선수만 뽑기
        List<Player> players = playerRepository.findByStatusAndPosition(PlayerStatus.ACTIVE, PlayerPosition.DF);

        List<Donation> donations = donationRepository.getDonationAllDF();

        Map<Integer, List<Donation>> donationMap = donations.stream()
                .collect(Collectors.groupingBy(d -> d.getPlayer().getId()));

        return players.stream()
                .map(p -> {
                    List<Donation> donationList = limitDonation(p, donationMap);
                    HashMap<Integer, String> nicknameList = new HashMap<>();
                    RankNicknameList(donationList, nicknameList);
                    return new PlayerResponseRank(p, nicknameList);
                })
                .toList();
    }

    public List<PlayerResponseRank> getAllPlayersGK() {
        // 포지션 GK인 선수만 뽑기
        List<Player> players = playerRepository.findByStatusAndPosition(PlayerStatus.ACTIVE, PlayerPosition.GK);

        List<Donation> donations = donationRepository.getDonationAllGK();

        Map<Integer, List<Donation>> donationMap = donations.stream()
                .collect(Collectors.groupingBy(d -> d.getPlayer().getId()));

        return players.stream()
                .map(p -> {
                    List<Donation> donationList = limitDonation(p, donationMap);
                    HashMap<Integer, String> nicknameList = new HashMap<>();
                    RankNicknameList(donationList, nicknameList);
                    return new PlayerResponseRank(p, nicknameList);
                })
                .toList();
    }

    /**
     * 플레이어 기준으로 그룹핑된 도네이션들중 포인트를 내림차순으로 정렬 후 상위 3개만 반환하는 메서드
     * @param p : Player 객체
     * @param donaMap : <player.id, Donation> 으로 이루어진 그루핑된 HashMap
     * @return
     */
    private static List<Donation> limitDonation(Player p, Map<Integer, List<Donation>> donaMap) {
        return donaMap.getOrDefault(p.getId(), List.of()).stream()
                .sorted((a, b) -> Integer.compare(b.getPoint(), a.getPoint()))
                .limit(3)
                .toList();
    }

    /**
     * 1 : user1, 2 : user2 랭킹 및 유저닉네임을 담기위해 만들어진 HashMap
     * @param donationList : 플레이어 기준으로 그룹핑된 도네이션들중 포인트를 내림차순으로 정렬 후 상위 3개
     * @param nicknameList : 들어올때는 빈 HashMap
     */
    private static void RankNicknameList(List<Donation> donationList, HashMap<Integer, String> nicknameList) {
        for (int i = 0; i < 3; i++) { // 랭킹은 3위까지만 필요
            if (i + 1 > donationList.size()) {
                nicknameList.put(i + 1, "null");
            } else {
                nicknameList.put(i + 1, donationList.get(i).getNickname());
            }
        }
    }


    /** 여기서부터는 나중에 쓸 수도 있는 것 */

    // 현역 + FW 선수 전체 조회
    public List<PlayerResponse> getAllFWActivePlayers() {
        return playerRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() == PlayerStatus.ACTIVE)
                .filter(p -> p.getPosition() == PlayerPosition.FW)
                .map(p -> new PlayerResponse(p))
                .toList();
    }

    // 현역 + MF 선수 전체 조회
    public List<PlayerResponse> getAllMFActivePlayers() {
        return playerRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() == PlayerStatus.ACTIVE)
                .filter(p -> p.getPosition() == PlayerPosition.MF)
                .map(p -> new PlayerResponse(p))
                .toList();
    }

    // 현역 + DF 선수 전체 조회
    public List<PlayerResponse> getAllDFActivePlayers() {
        return playerRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() == PlayerStatus.ACTIVE)
                .filter(p -> p.getPosition() == PlayerPosition.DF)
                .map(p -> new PlayerResponse(p))
                .toList();
    }

    // 현역 + GK 선수 전체 조회
    public List<PlayerResponse> getAllGKActivePlayers() {
        return playerRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() == PlayerStatus.ACTIVE)
                .filter(p -> p.getPosition() == PlayerPosition.GK)
                .map(p -> new PlayerResponse(p))
                .toList();
    }

    // 선수 삭제
    @Transactional
    @CacheEvict(value = "allPlayers", allEntries = true)
    public void deletePlayer(long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "404", "NOT_FOUND", "존재하지 않은 선수입니다."));
        playerRepository.delete(player);
    }


}
