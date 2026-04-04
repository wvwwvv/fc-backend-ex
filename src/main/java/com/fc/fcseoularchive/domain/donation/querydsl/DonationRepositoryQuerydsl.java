package com.fc.fcseoularchive.domain.donation.querydsl;

import com.fc.fcseoularchive.domain.donation.Donation;

import java.util.List;

public interface DonationRepositoryQuerydsl {

    List<Donation> getDonationAll();

    List<Donation> getDonationAllFW();

    List<Donation> getDonationAllMF();

    List<Donation> getDonationAllDF();

    List<Donation> getDonationAllGK();

}
