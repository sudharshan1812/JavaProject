package com.smarttoll.rfid;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RFIDReader {

    String READER_ID = "R420-001";

    record RFIDEvent(String tagId, LocalDateTime scannedAt) {
    }

    void start();

    void stop();

    boolean isRunning();

    Optional<RFIDEvent> readTag(String tagId);
}