package com.mydaytodo.sfa.asset.jobs;

import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import com.mydaytodo.sfa.asset.repository.ScheduleRepository;
import com.mydaytodo.sfa.asset.service.MailService;
import com.mydaytodo.sfa.asset.utilities.DateTimeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Slf4j
public class EmailSchedulerJobTest {
    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private MailService mailService;

    @InjectMocks
    private EmailSchedulerJob emailSchedulerJob;
    private List<Schedule> mockSchedules;
    @BeforeEach
    void setup() {
        mockSchedules = new ArrayList<>();
        String timeWindow = DateTimeService.getTimeWindow();

        Schedule ms1 = Schedule.builder()
                .id("SCH_123")
                .isRecurring(false)
                .username("bhuman@mydaytodo.com")
                .sender("Bhuman Soni")
                .timeWindow(timeWindow)
                .receivers(List.of("qin@gmail.com", "kang@gmail.com"))
                .filename("sample1.pdf")
                .isSent(true)
                .build();

        Schedule ms2 = Schedule.builder()
                .isRecurring(false)
                .id("SCH_345")
                .username("bhuman@mydaytodo.com")
                .sender("Bhuman Soni")
                .timeWindow(timeWindow)
                .receivers(List.of("qin@gmail.com", "kang@gmail.com"))
                .filename("sample1.pdf")
                .isSent(false)
                .build();
        mockSchedules.add(ms1);
        mockSchedules.add(ms2);
    }
    @Test
    void testSendScheduledEmails_success() throws InterruptedException {
        when(scheduleRepository.findAll()).thenReturn(mockSchedules);
        doNothing().when(scheduleRepository).updateScheduleIsSent(anyString(), anyBoolean());
        emailSchedulerJob.sendScheduledEmails();
        verify(scheduleRepository, times(1)).findAll();
        Thread.sleep(100);
        verify(mailService, times(1)).sendEmail(any());
        verify(scheduleRepository, times(1)).updateScheduleIsSent(anyString(), anyBoolean());
    }
}
