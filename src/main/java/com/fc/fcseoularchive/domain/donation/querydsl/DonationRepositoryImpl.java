package com.fc.fcseoularchive.domain.donation.querydsl;


import com.fc.fcseoularchive.domain.donation.Donation;
import com.fc.fcseoularchive.domain.player.PlayerPosition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.fc.fcseoularchive.domain.donation.QDonation.donation;
import static com.fc.fcseoularchive.domain.player.QPlayer.player;
import static com.fc.fcseoularchive.domain.user.QUser.user;


@RequiredArgsConstructor
public class DonationRepositoryImpl implements DonationRepositoryQuerydsl{

    private final JPAQueryFactory jpaqueryFactory;

    /** donation 기준 user, player 한번에 다 가져오기*/
    @Override
    public List<Donation> getDonationAll() {
        return jpaqueryFactory
                .select(donation)
                .from(donation)
                .leftJoin(user, user).fetchJoin()
                .leftJoin(player, player).fetchJoin()
                .fetch();
    }

    @Override
    public List<Donation> getDonationAllFW() {
        return jpaqueryFactory
                .select(donation)
                .from(donation)
                .leftJoin(user, user).fetchJoin()
                .leftJoin(player, player).fetchJoin()
                .where(player.position.eq(PlayerPosition.FW))
                .fetch();
    }

    @Override
    public List<Donation> getDonationAllMF() {
        return jpaqueryFactory
                .select(donation)
                .from(donation)
                .leftJoin(user, user).fetchJoin()
                .leftJoin(player, player).fetchJoin()
                .where(player.position.eq(PlayerPosition.MF))
                .fetch();
    }

    @Override
    public List<Donation> getDonationAllDF() {
        return jpaqueryFactory
                .select(donation)
                .from(donation)
                .leftJoin(user, user).fetchJoin()
                .leftJoin(player, player).fetchJoin()
                .where(player.position.eq(PlayerPosition.DF))
                .fetch();
    }

    @Override
    public List<Donation> getDonationAllGK() {
        return jpaqueryFactory
                .select(donation)
                .from(donation)
                .leftJoin(user, user).fetchJoin()
                .leftJoin(player, player).fetchJoin()
                .where(player.position.eq(PlayerPosition.GK))
                .fetch();
    }
}
