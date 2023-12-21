package com.jeroscalmera.battleship_project.models;

import javax.persistence.*;

@Entity
@Table(name = "bugreports")
public class BugReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "report")
    private String report;

    public BugReport(String report) {
        this.report = report;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}


