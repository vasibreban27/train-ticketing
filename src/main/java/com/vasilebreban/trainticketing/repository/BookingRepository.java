package com.vasilebreban.trainticketing.repository;

import com.vasilebreban.trainticketing.model.Booking;
import com.vasilebreban.trainticketing.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Station> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);

    //return 0 if null or the number of tickets
    @Query("SELECT COALESCE(SUM(b.numberOfTickets), 0) FROM Booking b WHERE b.train.id = :trainId")
    Integer countBookedSeatsByTrainId(Long trainId);
}
