# Smart RFID Toll System

A full-stack RFID toll booth control system: **Spring Boot (Java 17) REST API + MySQL/H2** backend,
**HTML/CSS/JS dashboard** frontend, RFID tag scanning (simulated reader), dynamic congestion toll
pricing, multi-method payments and digital receipts.

```
                     SMART TOLL SYSTEM
        ┌──────────────────┼──────────────────┐
        │                  │                  │
   FRONTEND            BACKEND             DATABASE
   (HTML dashboard)   Spring Boot          H2 / MySQL
   Chart.js       Controllers/Services
                    Pricing Engine
                    RFID + Payment Engine
```

## Quick start

```bash
# 1. backend  (requires Java 17+ and Maven; mvn is optional - see below)
cd backend
mvn spring-boot:run            # or: mvn -DskipTests package && java -jar target/smart-toll-backend-1.0.0.jar

# 2. frontend (any static server; or just open frontend/index.html)
cd ../frontend
python3 -m http.server 5500

# 3. open http://localhost:5500  ->  login: admin / admin123
```

Maven is bundled locally at `~/tools/apache-maven-3.9.11` if it is not on your PATH.

## Demo tour

| Page | What to try |
|---|---|
| Dashboard | KPI cards, live toll activity table (auto-refreshes), quick RFID scan |
| Vehicles | Search, view, edit, delete; register new vehicles |
| RFID Control | Start/stop reader, create tags, activate/deactivate, **simulate a tag scan** |
| Toll Pricing | See live conditions (traffic/weather/pollution/peak) and per-type breakdown |
| Transactions | All crossings with dynamic surcharge split; date-range filter |
| Payments | Pay pending transactions via UPI/Card/Wallet, view digital receipt |
| Reports | Revenue KPIs + Chart.js charts (daily revenue, vehicle/payment distribution) |
| Settings | Manually change pricing conditions -> next scan reprices instantly |

**End-to-end flow:** RFID scan -> vehicle identified -> dynamic pricing -> PENDING transaction ->
payment -> PAID + receipt file written to `backend/receipts/TXN-*.txt`.

## Architecture

```
backend/src/main/java/com/smarttoll/
├── config/      Security (JWT), CORS, DataSeeder (demo data)
├── controller/  Auth, Vehicle, RFID(+scan), Toll, Payment, Transaction, Dashboard, Report
├── service/     Business logic, RFID scan flow, payment orchestration
├── repository/  Spring Data JPA repositories
├── model/       Vehicle (JPA inheritance: Car/Truck/Bus/EmergencyVehicle), RFIDTag,
│                User, TollTransaction, Payment, enums
├── pricing/     TrafficMonitor, WeatherService, PollutionMonitor, PeakHourService,
│                DynamicPricing (Strategy pattern), TollBreakdown, PricingStrategy,
│                TollCalculator (functional interface)
├── rfid/        RFIDReader interface + RFIDSimulator (swap for real Impinj reader)
├── payment/     PaymentProcessor interface + UPI/Card/Wallet processors
├── dto/         Request/response records
├── exception/   GlobalExceptionHandler + typed exceptions
├── security/    JWT service + authentication filter
└── util/        ReceiptGenerator (file I/O), DateTimeUtil
```

## API overview

```
POST /api/auth/login                    -> JWT token (admin/admin123)
GET  /api/vehicles?q=                   CRUD /api/vehicles/{id}
GET  /api/rfid | POST /api/rfid  (create tag) | POST /api/rfid/scan
POST /api/rfid/{tagId}/activate|deactivate | /api/rfid/status | /api/rfid/reader/start|stop
POST /api/toll/calculate | GET /api/toll/current-rates | PUT /api/toll/conditions
GET  /api/transactions | /api/transactions/pending
POST /api/payments {transactionId, method}
GET  /api/dashboard/statistics | /api/dashboard/live-transactions
GET  /api/reports/revenue | /vehicle-distribution | /payment-methods | /daily-revenue?days=7
```

Authenticate every request (except login) with `Authorization: Bearer <token>`.

## Databases

- **Default (demo):** H2 file DB at `backend/data/` - zero setup, survives restarts.
- **MySQL:** `--spring.profiles.active=prod` with env vars `DB_HOST/DB_PORT/DB_NAME/DB_USER/DB_PASSWORD`.
  Reference DDL + seed in `database/schema.sql` / `database/seed.sql`.

## dynamic pricing model

```
Base toll (CAR 80 / TRUCK 180 / BUS 120 / EMERGENCY 0)
  + traffic surcharge   (LOW 10% / MEDIUM 25% / HIGH 50%)
  + weather surcharge   (Sunny 0 / Rain 10 / Fog 15 / Storm 25 %)
  + pollution surcharge (Good 0 / Moderate 5 / Poor 10 / Severe 20 %)
  + peak-hour flat      (+₹15, 8-11 AM & 5-9 PM)
  = FINAL TOLL
```

## Java concepts covered

Where each classic Java topic lives in this codebase:

| Topic | Where |
|---|---|
| **Classes & objects** | Entity classes in `model/` (`Vehicle`, `User`, `TollTransaction`, `Payment`, `RFIDTag`) with fields + getters/setters; `@Service`/`@Component` objects created and wired by Spring via constructor injection (`service/TollPricingService.java`) |
| **Inheritance & interfaces** | `Vehicle` is an abstract base class with abstract `type()` (`model/Vehicle.java:22,44`); subclasses `Car`/`Truck`/`Bus`/`EmergencyVehicle` extend it (`model/Car.java:10`), persisted as JPA single-table inheritance (`model/Vehicle.java:13-14`). Interfaces: `PaymentProcessor` (UPI/Card/Wallet), `RFIDReader` (simulator vs real hardware), `PricingStrategy` (traffic/weather/pollution), functional interface `TollCalculator` (`pricing/TollCalculator.java`) used as a lambda (`pricing/DynamicPricing.java:49`) |
| **Exception handling & I/O** | Typed runtime exceptions in `exception/` (`PaymentException`, `VehicleNotFoundException`, `RFIDNotFoundException`) mapped to HTTP statuses centrally by `GlobalExceptionHandler` (`exception/GlobalExceptionHandler.java:22-59`); checked `IOException` caught around receipt writing (`service/PaymentService.java:67-71`). File I/O via `java.nio.file.Files` in `util/ReceiptGenerator.java` - receipts land in `backend/receipts/TXN-*.txt` |
| **Multithreading** | `@EnableScheduling` on `SmartTollApplication` + `@Scheduled` background task `pricing/ConditionSimulator.java` that fluctuates traffic/weather/pollution every 20s on a dedicated scheduler thread, while HTTP requests run on their own servlet threads (see `JwtAuthenticationFilter` - a `OncePerRequestFilter`); `ThreadLocalRandom` used in `PaymentService`/`DataSeeder` |
| **Collections & database** | `Map<String, PaymentProcessor>` strategy lookup (`service/PaymentService.java:28,56`), `LinkedHashMap` aggregation + streams in `service/ReportService.java`. Database: Spring Data JPA repositories (`repository/*.java`) over H2 (default) or MySQL (`--spring.profiles.active=prod`), entities mapped with `@Entity`/`@Id`, demo data seeded by `config/DataSeeder.java` |

## Swapping the RFID simulator for hardware

`RFIDSimulator` implements `RFIDReader` (`start/stop/readTag`). Write a `HardwareRFIDReader`
implementing the same interface (e.g. Impinj over LLRP, MQTT or a serial/network gateway), annotate
it `@Component`, and the application switches over - controllers/services depend only on the
interface.

## Server note

Java 25 is installed here; the build targets Java 17 bytecode (`maven.compiler.release=17`) and runs
fine on newer JDKs. Spring Boot 3.5.x is used.

## Project layout

```
smart-toll-system/
├── backend/     Spring Boot app (Maven)
├── frontend/    static dashboard (pages/, css/, js/, assets/vendor/chart.umd.min.js)
├── database/    schema.sql + seed.sql (MySQL reference)
└── docs/        (this README lives at the root)
```

There is also an older console-only prototype in `~/tollbooth/` - its concepts (module1_vehicle,
module2_pricing, module3_payment) were re-implemented here as a production-style web application.