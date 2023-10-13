package com.jeroscalmera.battleship_project.repositories;

import com.jeroscalmera.battleship_project.models.Room;
import com.jeroscalmera.battleship_project.websocket.GameData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    void deleteAll();
    @Query
    Room findByRoomNumber(String roomNumber);
}