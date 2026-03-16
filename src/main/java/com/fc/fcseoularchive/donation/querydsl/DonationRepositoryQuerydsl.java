package com.fc.fcseoularchive.donation.querydsl;

import com.fc.fcseoularchive.domain.entity.Donation;

import java.util.List;

public interface DonationRepositoryQuerydsl {

    List<Donation> getDonationAll();

    List<Donation> getDonationAllFW();

    List<Donation> getDonationAllMF();

    List<Donation> getDonationAllDF();

    List<Donation> getDonationAllGK();

}
