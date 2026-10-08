package com.smarttoll.repository;

import com.smarttoll.model.RFIDTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RFIDTagRepository extends JpaRepository<RFIDTag, Long> {

    Optional<RFIDTag> findByTagId(String tagId);

    List<RFIDTag> findAllByOrderByIdDesc();
}