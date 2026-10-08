package com.smarttoll.model;

import com.smarttoll.model.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TRUCK")
public class Truck extends Vehicle {

    @Column(name = "load_capacity")
    private Double loadCapacity;

    @Override
    public VehicleType type() {
        return VehicleType.TRUCK;
    }

    public Double getLoadCapacity() { return loadCapacity; }
    public void setLoadCapacity(Double loadCapacity) { this.loadCapacity = loadCapacity; }
}