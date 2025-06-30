package com.shadril.email_api_demo.repository;

import com.shadril.email_api_demo.entity.HistoryLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryLogRepository extends JpaRepository<HistoryLogEntity, Long> {
}