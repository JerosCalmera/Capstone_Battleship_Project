package com.jeroscalmera.battleship_project.repositories;

import com.jeroscalmera.battleship_project.models.Player;
import com.jeroscalmera.battleship_project.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    void deleteAll();
    @Query
    Room findByRoomNumber(String roomNumber);
    @Query
    Room findRoomIdByPlayersName(String name);
    @Query
    Room findRoomIdByPlayersId(Long id);
}