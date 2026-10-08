package com.smarttoll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rfid_tags")
public class RFIDTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_id", unique = true, nullable = false)
    private String tagId;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "issue_date")
    private LocalDateTime issueDate = LocalDateTime.now();

    @OneToOne(mappedBy = "rfidTag")
    @JsonIgnore
    private Vehicle vehicle;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTagId() { return tagId; }
    public void setTagId(String tagId) { this.tagId = tagId; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
}