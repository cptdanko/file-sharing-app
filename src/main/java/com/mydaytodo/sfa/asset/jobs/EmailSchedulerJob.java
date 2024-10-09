package com.mydaytodo.sfa.asset.jobs;

import com.amazonaws.util.DateUtils;
import com.mydaytodo.sfa.asset.model.EmailRequest;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import com.mydaytodo.sfa.asset.repository.ScheduleRepository;
import com.mydaytodo.sfa.asset.service.MailService;
import com.mydaytodo.sfa.asset.utilities.DateTimeService;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class EmailSchedulerJob {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MailService mailService;
    /**
     * Get all the schedules that haven't been sent
     * isolate those that fall in the current timeframe
     * iterate through them and create an async task
     * to send these emails
     */
    @Async
    @Scheduled(cron = "* */45 * * * *")
    public void sendScheduledEmails() {
        List<Schedule> toBeSent = new ArrayList<>();
        String currentRange = DateTimeService.getTimeWindow();
        log.info("About to get schedules for current hour {} , {}", currentRange, new Date().toString());

        scheduleRepository.findAll().forEach(schedule -> {
            if(schedule.getTimeWindow().equalsIgnoreCase(currentRange)
            && !schedule.getIsSent()) {
                toBeSent.add(schedule);
            }
        });
        log.info("About to trigger async job to send emails");
        scheduleAsyncJob(toBeSent);
    }
    public void scheduleAsyncJob(List<Schedule> schedules) {
        schedules.forEach(schedule -> {
            log.info("queuing up the following [ {} ] schedule to be sent", schedule.toString());
            final String message = String.format("%s shared documents with you, check attachments in mail", schedule.getSender());
            CompletableFuture.runAsync(() -> {
                String emailSubject = String.format("%s has shared %s with you", schedule.getUsername(), schedule.getFilename());
                log.info("About to send email with message {}", emailSubject);
                EmailRequest request = EmailRequest.builder()
                        .from(schedule.getUsername())
                        .to(schedule.getReceivers().get(0))
                        .filesToAttach(new String[]{schedule.getFilename()})
                        .subject(emailSubject)
                        .body(message)
                        .build();
                mailService.sendEmail(request);
                scheduleRepository.updateScheduleIsSent(schedule.getId(), true);
            });
        });
    }
}
