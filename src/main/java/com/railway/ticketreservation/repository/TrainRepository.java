package com.railway.ticketreservation.repository;

import com.railway.ticketreservation.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface TrainRepository extends JpaRepository<Train, Integer> {
    @Query("SELECT t FROM Train t WHERE t.trainNumber = :trainNumber")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Train> findByIdForUpdate(@Param("trainNumber") int trainNumber);
}