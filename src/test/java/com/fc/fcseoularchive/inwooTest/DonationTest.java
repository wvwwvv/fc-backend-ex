//package com.fc.fcseoularchive.inwooTest;
//
//import com.fc.fcseoularchive.domain.donation.Donation;
//import com.fc.fcseoularchive.domain.player.Player;
//import com.fc.fcseoularchive.donation.DonationRepository;
//import com.fc.fcseoularchive.player.PlayerRepository;
//import org.hibernate.validator.internal.constraintvalidators.bv.time.future.FutureValidatorForInstant;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@SpringBootTest
//public class DonationTest {
//
//    @Autowired
//    private DonationRepository donationRepository;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Test
//    @DisplayName("후원 <-> 유저 fetchJoin 테스트")
//    public void test1() throws Exception {
//
//        List<Donation> donationAll = donationRepository.getDonationAll();
//        for (Donation donation : donationAll) {
//            System.out.println("donation = " + donation.getId());
//            System.out.println("donation.getPoint() = " + donation.getPoint());
//            System.out.println("donation.getUser().getNickname() = " + donation.getUser().getNickname());
//            System.out.println("==============");
//        }
//
//    }
//
//    @Test
//    @DisplayName("선수 한방에 불러오기")
//    public void test2() throws Exception{
//        List<Player> players = playerRepository.findAll();
//
//        for (Player player : players) {
//            System.out.println("player = " + player.getName());
//        }
//    }
//
//    @Test
//    @DisplayName("도네이션 fetch join 테스트")
//    public void test3() throws Exception{
//
//        List<Donation> donationAll = donationRepository.getDonationAll();
//
//        for (Donation donation : donationAll) {
//            System.out.println("donation.getUser().getNickname() = " + donation.getUser().getNickname());
//            System.out.println("donation.getPlayer().getName() = " + donation.getPlayer().getName());
//            System.out.println(" = ======== =");
//        }
//
//    }
//
//    @Test
//    @DisplayName("donation 한번에 가져와서 player로 그루핑 테스트")
//    public void test4() throws Exception{
//
//        Map<Integer, List<Donation>> collect = donationRepository.getDonationAll().stream()
//                .collect(Collectors.groupingBy(d -> d.getPlayer().getId()));
//
//        for (Integer i : collect.keySet()) {
//            List<Donation> donations = collect.get(i);
//            for (Donation donation : donations) {
//                System.out.println("donation = " + donation.getId());
//            }
//        }
//    }
//
//    @Test
//    @DisplayName("player 뽑는데 후원한 사람들 함께 가져오기")
//    public void test5() throws Exception{
//
//        List<Player> players = playerRepository.findAll();
//
//        List<Donation> donations = donationRepository.getDonationAll();
//
//        Map<Integer, List<Donation>> map = donations.stream()
//                .collect(Collectors.groupingBy(d -> d.getPlayer().getId()));
//
//        for (Player player : players) {
//
//            System.out.println("player = " + player.getName());
//
//            // 해당 플레이어한테 후원된 사람들이 있다면, 해당 리스트에서 후원 많이 한사람들로 정렬 후 상위 3명 뽑아서 리스트로 만들기
//            List<Donation> list = map.getOrDefault(player.getId(), List.of()).stream()
//                    .sorted((a, b) -> Integer.compare(b.getPoint(), a.getPoint()))
//                    .limit(3)
//                    .toList();
//
//            // 해당 리스트 크기 만큼 사람들 꺼내오기 (최대는 3)
////            for (Donation donation : list) {
////                System.out.println(donation.getUser().getNickname());
////            }
//
//            // 업그레이드 버전으로 없다면 null 출력해주기
////            for(int i=0; i<3; i++){
////               if(i+1 > list.size()){
////                   System.out.println("null");
////               } else {
////                   System.out.println(list.get(i).getUser().getNickname());
////               }
////            }
//
//            // 더 업그레이드 버전으로 1 : user, 2 : null 이런식으로 Map으로 만들어서 출력해주기
//            HashMap<Integer, String> donaUsers = new HashMap<>();
//            for(int i=0; i<3; i++){
//                if(i+1 > list.size()){
//                    donaUsers.put(i+1, "null");
//                } else {
//                    donaUsers.put(i+1, list.get(i).getUser().getNickname());
//                }
//            }
//
//            for (Integer i : donaUsers.keySet()) {
//                System.out.println(i+" : "+donaUsers.get(i));
//            }
//
//
//            System.out.println("====================" );
//
//
//        }
//
//
//    }
//
//
//
//
//}
