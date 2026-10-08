package com.smarttoll.controller;

import com.smarttoll.dto.*;
import com.smarttoll.model.RFIDTag;
import com.smarttoll.model.Vehicle;
import com.smarttoll.service.RFIDService;
import com.smarttoll.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VehicleController {

    private final VehicleService vehicleService;
    private final RFIDService rfidService;

    public VehicleController(VehicleService vehicleService, RFIDService rfidService) {
        this.vehicleService = vehicleService;
        this.rfidService = rfidService;
    }

    @GetMapping("/vehicles")
    public List<Vehicle> list(@RequestParam(required = false) String q) {
        return vehicleService.findAll(q);
    }

    @GetMapping("/vehicles/{id}")
    public Vehicle get(@PathVariable Long id) {
        return vehicleService.get(id);
    }

    @PostMapping("/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle create(@Valid @RequestBody VehicleRequest request) {
        return vehicleService.create(request);
    }

    @PutMapping("/vehicles/{id}")
    public Vehicle update(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        return vehicleService.update(id, request);
    }

    @DeleteMapping("/vehicles/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        vehicleService.delete(id);
    }

    @GetMapping("/rfid")
    public List<RFIDTagDTO> listTags() {
        return rfidService.listTags();
    }

    @GetMapping("/rfid/status")
    public com.smarttoll.dto.ReaderStatus readerStatus() {
        return rfidService.readerStatus();
    }

    @PostMapping("/rfid/reader/start")
    public com.smarttoll.dto.ReaderStatus startReader() {
        rfidService.startReader();
        return rfidService.readerStatus();
    }

    @PostMapping("/rfid/reader/stop")
    public com.smarttoll.dto.ReaderStatus stopReader() {
        rfidService.stopReader();
        return rfidService.readerStatus();
    }

    @PostMapping("/rfid")
    @ResponseStatus(HttpStatus.CREATED)
    public RFIDTag createTag(@RequestBody Map<String, String> body) {
        return rfidService.createTag(body.get("tagId"));
    }

    @PostMapping("/rfid/scan")
    public ScanResult scan(@Valid @RequestBody RFIDScanRequest request) {
        return vehicleService.scan(request);
    }

    @PostMapping("/rfid/{tagId}/activate")
    public RFIDTag activate(@PathVariable String tagId) {
        return rfidService.setActive(tagId, true);
    }

    @PostMapping("/rfid/{tagId}/deactivate")
    public RFIDTag deactivate(@PathVariable String tagId) {
        return rfidService.setActive(tagId, false);
    }
}