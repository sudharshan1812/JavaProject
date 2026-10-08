package com.smarttoll.model;

import com.smarttoll.model.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BUS")
public class Bus extends Vehicle {

    @Column(name = "passenger_capacity")
    private Integer passengerCapacity;

    @Override
    public VehicleType type() {
        return VehicleType.BUS;
    }

    public Integer getPassengerCapacity() { return passengerCapacity; }
    public void setPassengerCapacity(Integer passengerCapacity) { this.passengerCapacity = passengerCapacity; }
}