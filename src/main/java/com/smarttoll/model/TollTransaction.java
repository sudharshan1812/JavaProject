package com.smarttoll.model;

import com.smarttoll.model.enums.TransactionStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "toll_transactions")
public class TollTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(name = "rfid_tag_id")
    private String rfidTagId;

    @Column(name = "base_toll")
    private double baseToll;

    @Column(name = "traffic_charge")
    private double trafficCharge;

    @Column(name = "peak_charge")
    private double peakCharge;

    @Column(name = "weather_charge")
    private double weatherCharge;

    @Column(name = "pollution_charge")
    private double pollutionCharge;

    @Column(name = "final_amount")
    private double finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public String getRfidTagId() { return rfidTagId; }
    public void setRfidTagId(String rfidTagId) { this.rfidTagId = rfidTagId; }
    public double getBaseToll() { return baseToll; }
    public void setBaseToll(double baseToll) { this.baseToll = baseToll; }
    public double getTrafficCharge() { return trafficCharge; }
    public void setTrafficCharge(double trafficCharge) { this.trafficCharge = trafficCharge; }
    public double getPeakCharge() { return peakCharge; }
    public void setPeakCharge(double peakCharge) { this.peakCharge = peakCharge; }
    public double getWeatherCharge() { return weatherCharge; }
    public void setWeatherCharge(double weatherCharge) { this.weatherCharge = weatherCharge; }
    public double getPollutionCharge() { return pollutionCharge; }
    public void setPollutionCharge(double pollutionCharge) { this.pollutionCharge = pollutionCharge; }
    public double getFinalAmount() { return finalAmount; }
    public void setFinalAmount(double finalAmount) { this.finalAmount = finalAmount; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}