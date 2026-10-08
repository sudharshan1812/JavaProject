package com.smarttoll.service;

import com.smarttoll.dto.RFIDTagDTO;
import com.smarttoll.dto.ReaderStatus;
import com.smarttoll.exception.RFIDNotFoundException;
import com.smarttoll.model.RFIDTag;
import com.smarttoll.repository.RFIDTagRepository;
import com.smarttoll.rfid.RFIDReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RFIDService {

    private final RFIDTagRepository tagRepository;
    private final RFIDReader rfidReader;

    public RFIDService(RFIDTagRepository tagRepository, RFIDReader rfidReader) {
        this.tagRepository = tagRepository;
        this.rfidReader = rfidReader;
    }

    public ReaderStatus readerStatus() {
        return new ReaderStatus(RFIDReader.READER_ID, rfidReader.isRunning());
    }

    public void startReader() {
        rfidReader.start();
    }

    public void stopReader() {
        rfidReader.stop();
    }

    public List<RFIDTagDTO> listTags() {
        return tagRepository.findAllByOrderByIdDesc().stream()
                .map(RFIDTagDTO::of)
                .toList();
    }

    @Transactional
    public RFIDTag createTag(String tagId) {
        tagRepository.findByTagId(tagId.trim())
                .ifPresent(t -> {
                    throw new IllegalArgumentException("Tag already exists: " + t.getTagId());
                });
        RFIDTag tag = new RFIDTag();
        tag.setTagId(tagId.trim().toUpperCase());
        return tagRepository.save(tag);
    }

    @Transactional
    public RFIDTag setActive(String tagId, boolean active) {
        RFIDTag tag = tagRepository.findByTagId(tagId)
                .orElseThrow(() -> new RFIDNotFoundException("RFID tag not found: " + tagId));
        tag.setActive(active);
        return tagRepository.save(tag);
    }
}