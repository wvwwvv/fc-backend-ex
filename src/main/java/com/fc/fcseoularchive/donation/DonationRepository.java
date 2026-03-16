package com.fc.fcseoularchive.donation;


import com.fc.fcseoularchive.domain.entity.Donation;
import com.fc.fcseoularchive.donation.querydsl.DonationRepositoryQuerydsl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<Donation,Long> , DonationRepositoryQuerydsl {

    @Query("SELECT d" +
            " FROM Donation d" +
            " JOIN FETCH d.user u" +
            " JOIN FETCH d.player p" +
            " WHERE u.id = :userId" +
            " AND p.id = :playerId")
    public Optional<Donation> findByUserIdAndPlayerId(@Param("userId") Long userId, @Param("playerId") Long playerId);

}
