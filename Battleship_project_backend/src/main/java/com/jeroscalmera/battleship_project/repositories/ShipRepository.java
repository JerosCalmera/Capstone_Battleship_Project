package com.jeroscalmera.battleship_project.repositories;

import com.jeroscalmera.battleship_project.models.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long> {
    void deleteAll();
    @Query("SELECT s.id FROM Ship s WHERE s.coOrds LIKE %:target%")
    Long findShipIdsByCoOrdsContainingPair(@Param("target") String target);

    @Query("SELECT s.coOrds FROM Ship s")
    List<String> findAllCoOrds();

    @Query("SELECT s.coOrds FROM Ship s WHERE s.player.name = :playerName")
    List<String> findAllCoOrdsByPlayerName(@Param("playerName") String playerName);
}
