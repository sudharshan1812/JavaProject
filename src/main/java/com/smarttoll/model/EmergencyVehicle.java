package com.smarttoll.model;

import com.smarttoll.model.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EMERGENCY")
public class EmergencyVehicle extends Vehicle {

    @Column(name = "emergency_service")
    private String emergencyService;

    @Override
    public VehicleType type() {
        return VehicleType.EMERGENCY;
    }

    public String getEmergencyService() { return emergencyService; }
    public void setEmergencyService(String emergencyService) { this.emergencyService = emergencyService; }
}