package com.jeroscalmera.battleship_project.repositories;

import com.jeroscalmera.battleship_project.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    void deleteAll();
}
