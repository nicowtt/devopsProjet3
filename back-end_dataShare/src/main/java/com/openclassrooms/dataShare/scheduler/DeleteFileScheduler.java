package com.openclassrooms.dataShare.scheduler;

import com.openclassrooms.dataShare.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteFileScheduler {

    private final FileService fileService;

    // run every day at 23pm to avoid surcharged hours.
    // users can see expired files just one day.
    @Scheduled(cron = "0 0 23 * * *")
    public void deleteExpiredFiles() {
        fileService.deleteExpiredFiles();
    }
}
