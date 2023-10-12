package com.jeroscalmera.battleship_project.repositories;

import com.jeroscalmera.battleship_project.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    void deleteAll();

    @Query("SELECT r.name FROM Player r")
    List<String> findName();
}
