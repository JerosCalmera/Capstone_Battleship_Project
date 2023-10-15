package com.jeroscalmera.battleship_project.repositories;

import com.jeroscalmera.battleship_project.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    void deleteAll();

    @Query("SELECT r.name FROM Player r")
    List<String> findName();

    @Query
    Player findByName(String name);

//    @Query("SELECT p FROM Player p WHERE p.name = :name")
//    Player findByName(@Param("name") String name);

    @Query("SELECT p.name FROM Player p WHERE p.room.roomNumber = :roomNumber")
    List<String> findPlayersByRoomNumber(@Param("roomNumber") String roomNumber);

}
