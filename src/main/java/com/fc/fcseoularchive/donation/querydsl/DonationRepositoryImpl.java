package com.fc.fcseoularchive.donation.querydsl;


import com.fc.fcseoularchive.domain.entity.Donation;
import com.fc.fcseoularchive.domain.entity.QDonation;
import com.fc.fcseoularchive.domain.entity.QPlayer;
import com.fc.fcseoularchive.domain.entity.QUser;
import com.fc.fcseoularchive.domain.enums.PlayerPosition;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.fc.fcseoularchive.domain.entity.QDonation.*;
import static com.fc.fcseoularchive.domain.entity.QPlayer.*;
import static com.fc.fcseoularchive.domain.entity.QUser.*;

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
