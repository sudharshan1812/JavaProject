package com.smarttoll.model;

import com.smarttoll.model.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {

    @Column(name = "number_of_seats")
    private Integer numberOfSeats;

    @Override
    public VehicleType type() {
        return VehicleType.CAR;
    }

    public Integer getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(Integer numberOfSeats) { this.numberOfSeats = numberOfSeats; }
}