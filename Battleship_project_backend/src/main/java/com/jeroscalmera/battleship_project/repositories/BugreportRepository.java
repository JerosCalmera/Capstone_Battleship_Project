package com.jeroscalmera.battleship_project.repositories;

import com.jeroscalmera.battleship_project.models.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BugreportRepository extends JpaRepository<BugReport, Long> {

    @Query
    BugReport findReportById(Long id);

}
