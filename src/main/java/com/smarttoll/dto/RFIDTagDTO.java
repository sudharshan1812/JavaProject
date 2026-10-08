package com.smarttoll.dto;

import com.smarttoll.model.RFIDTag;

import java.time.LocalDateTime;

public record RFIDTagDTO(
        Long id,
        String tagId,
        boolean active,
        LocalDateTime issueDate,
        String registrationNumber) {

    public static RFIDTagDTO of(RFIDTag tag) {
        return new RFIDTagDTO(
                tag.getId(),
                tag.getTagId(),
                tag.isActive(),
                tag.getIssueDate(),
                tag.getVehicle() != null ? tag.getVehicle().getRegistrationNumber() : null);
    }
}