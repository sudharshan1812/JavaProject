package com.smarttoll.repository;

import com.smarttoll.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    @Query("select v from Vehicle v where lower(v.registrationNumber) like lower(concat('%', :q, '%')) " +
           "or lower(v.ownerName) like lower(concat('%', :q, '%'))")
    List<Vehicle> search(String q);

    long countByActive(boolean active);
}