package com.nhnacademy.accountapi.scheduler;

import com.nhnacademy.accountapi.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DormantUserScheduler {
    private UserService userService;
    @Scheduled(cron = "0 0 0 * * *")
    public void convertToDormant() {
        userService.convertToDormant();
    }
}
