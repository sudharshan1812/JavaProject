package com.smarttoll.rfid;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class RFIDSimulator implements RFIDReader {

    private boolean running = true;

    @Override
    public void start() {
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public Optional<RFIDEvent> readTag(String tagId) {
        if (!running) {
            throw new IllegalStateException("RFID reader is stopped. Start the reader first.");
        }
        return Optional.of(new RFIDEvent(tagId, LocalDateTime.now()));
    }
}