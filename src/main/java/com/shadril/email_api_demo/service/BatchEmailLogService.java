package com.shadril.email_api_demo.service;

import com.shadril.email_api_demo.entity.HistoryLogEntity;
import com.shadril.email_api_demo.repository.HistoryLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchEmailLogService {

    private final HistoryLogRepository historyLogRepository;
    private final BlockingQueue<HistoryLogEntity> logQueue = new LinkedBlockingQueue<>(10000);

    public boolean queueLog(HistoryLogEntity historyLog) {
        // Non-blocking offer - returns false if queue is full rather than blocking
        return logQueue.offer(historyLog);
    }

    @Scheduled(fixedDelayString = "${email.log-batch-interval}")
    @Transactional
    public void processBatch() {
        List<HistoryLogEntity> batch = new ArrayList<>();
        logQueue.drainTo(batch, 100000);

        if (!batch.isEmpty()) {
            try {
                historyLogRepository.saveAll(batch);
                log.debug("Saved batch of {} email logs", batch.size());
            } catch (Exception e) {
                log.error("Failed to save batch of email logs: {}", e.getMessage(), e);
            }
        }
    }
}

