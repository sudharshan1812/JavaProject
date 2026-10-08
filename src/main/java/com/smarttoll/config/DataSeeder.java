package com.smarttoll.config;

import com.smarttoll.model.*;
import com.smarttoll.model.enums.*;
import com.smarttoll.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final RFIDTagRepository tagRepository;
    private final TollTransactionRepository transactionRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, VehicleRepository vehicleRepository,
                      RFIDTagRepository tagRepository, TollTransactionRepository transactionRepository,
                      PaymentRepository paymentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.tagRepository = tagRepository;
        this.transactionRepository = transactionRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdmin();
        if (vehicleRepository.count() > 0) {
            return;
        }
        seedVehicles();
        seedTransactions();
        log.info("Seeded demo data: admin user, vehicles, tags, 7 days of transactions");
    }

    private void seedAdmin() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }
    }

    private Vehicle withTag(Vehicle vehicle, String tagId) {
        RFIDTag tag = new RFIDTag();
        tag.setTagId(tagId);
        vehicle.setRfidTag(tag);
        return vehicleRepository.save(vehicle);
    }

    private void seedVehicles() {
        Car car1 = new Car();
        car1.setRegistrationNumber("TN01AB1234");
        car1.setOwnerName("Arun");
        car1.setNumberOfSeats(5);
        withTag(car1, "RFID001");

        Truck truck = new Truck();
        truck.setRegistrationNumber("TN02CD5678");
        truck.setOwnerName("Kumar");
        truck.setLoadCapacity(12.0);
        withTag(truck, "RFID002");

        Bus bus = new Bus();
        bus.setRegistrationNumber("TN03EF9012");
        bus.setOwnerName("Ravi");
        bus.setPassengerCapacity(48);
        withTag(bus, "RFID003");

        Car car2 = new Car();
        car2.setRegistrationNumber("TN04GH3456");
        car2.setOwnerName("Gobinath");
        car2.setNumberOfSeats(5);
        withTag(car2, "RFID004");

        EmergencyVehicle ambulance = new EmergencyVehicle();
        ambulance.setRegistrationNumber("TN05IJ7890");
        ambulance.setOwnerName("City Ambulance");
        ambulance.setEmergencyService("Ambulance");
        withTag(ambulance, "RFID005");

        Truck truck2 = new Truck();
        truck2.setRegistrationNumber("TN06KL2345");
        truck2.setOwnerName("Priya");
        truck2.setLoadCapacity(20.0);
        truck2.setActive(false);
        withTag(truck2, "RFID006");

        RFIDTag spare = new RFIDTag();
        spare.setTagId("RFID007");
        tagRepository.save(spare);
    }

    private void seedTransactions() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        int txIndex = 0;
        for (int day = 6; day >= 0; day--) {
            int count = ThreadLocalRandom.current().nextInt(3, 7);
            for (int i = 0; i < count; i++) {
                Vehicle vehicle = vehicles.get(ThreadLocalRandom.current().nextInt(vehicles.size()));
                double base = baseRate(vehicle.type());
                double traffic = round(base * 0.25);
                double weather = round(base * 0.10);
                double pollution = round(base * 0.05);
                double peak = ThreadLocalRandom.current().nextBoolean() ? 15.0 : 0.0;
                double total = base + traffic + weather + pollution + peak;

                TollTransaction tx = new TollTransaction();
                tx.setTransactionId("TXN-2026" + String.format("%02d%02d", now.getMonthValue(), Math.max(1, day + 1))
                        + "-" + (10000 + txIndex));
                tx.setVehicle(vehicle);
                tx.setRfidTagId(vehicle.getRfidTag() != null ? vehicle.getRfidTag().getTagId() : null);
                tx.setBaseToll(base);
                tx.setTrafficCharge(traffic);
                tx.setPeakCharge(peak);
                tx.setWeatherCharge(weather);
                tx.setPollutionCharge(pollution);
                tx.setFinalAmount(total);
                tx.setStatus(TransactionStatus.PAID);
                tx.setCreatedAt(now.minusDays(day).minusHours(ThreadLocalRandom.current().nextInt(0, 12))
                        .minusMinutes(ThreadLocalRandom.current().nextInt(0, 59)));
                transactionRepository.save(tx);

                Payment payment = new Payment();
                payment.setTransaction(tx);
                payment.setPaymentMethod(PaymentMethod.values()[ThreadLocalRandom.current().nextInt(3)]);
                payment.setPaymentId("PAY-" + (100000 + txIndex * 7 + i));
                payment.setAmount(total);
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setCreatedAt(tx.getCreatedAt());
                paymentRepository.save(payment);
                txIndex++;
            }
        }
    }

    private double baseRate(VehicleType type) {
        return switch (type) {
            case CAR -> 80.0;
            case TRUCK -> 180.0;
            case BUS -> 120.0;
            case EMERGENCY -> 0.0;
        };
    }

    private double round(double value) {
        return Math.round(value);
    }
}