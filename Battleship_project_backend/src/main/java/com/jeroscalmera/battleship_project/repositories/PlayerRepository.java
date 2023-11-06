package com.jeroscalmera.battleship_project.repositories;

import com.jeroscalmera.battleship_project.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    void deleteAll();

    @Query("SELECT r.name FROM Player r")
    List<String> findName();

    @Query
    Player findByName(String name);

    @Query
    Player findByPlayerNumber(String number);

    @Query
    Player findPlayerById(Long id);

    @Query("SELECT p.name FROM Player p WHERE p.room.roomNumber = :roomNumber")
    List<String> findPlayersByRoomNumber(@Param("roomNumber") String roomNumber);


    @Query("SELECT p FROM Player p WHERE p.name LIKE %:name%")
    Player findByNameContaining(@Param("name") String name);

    @Query("SELECT s.coOrds FROM Ship s WHERE s.player.name = :playerName")
    List<String> findAllCoOrdsByPlayerName(@Param("playerName") String playerName);


    @Query("SELECT s.coOrds FROM Ship s WHERE s.player.playerNumber = :playerNumber")
    List<String> findAllCoOrdsByPlayerNumber(@Param("playerNumber") String playerNumber);


    @Query
    String findAllCoOrdsById(Long id);

    @Query
    List<Player> findPlayersByRoomId(Long id);
}




