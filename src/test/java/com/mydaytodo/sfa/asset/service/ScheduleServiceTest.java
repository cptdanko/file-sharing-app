package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.error.EntityWithIdNotFoundException;
import com.mydaytodo.sfa.asset.error.InvalidScheduleException;
import com.mydaytodo.sfa.asset.error.ScheduleValidator;
import com.mydaytodo.sfa.asset.error.Validator;
import com.mydaytodo.sfa.asset.model.CreateScheduleRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import com.mydaytodo.sfa.asset.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ScheduleServiceTest {
    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;
    private final String MOCK_SCHEDULE_ID = "SCH_123";
    private final Schedule mockSchedule = Schedule.builder()
            .id(MOCK_SCHEDULE_ID)
            .sender("Bhuman Soni")
            .username("bhuman@mydaytodo.com")
            .build();

    private final CreateScheduleRequest createScheduleRequest = CreateScheduleRequest.builder()
            .isRecurring(false)
            .senderName("Bhuman Soni")
            .senderEmail("bhuman@mydaytodo.com")
            .timeWindow("12-13")
            .receivers(new ArrayList<>() {{ add("bhuman@mydaytodo.com"); add("bhuman.soni@gmail.com"); }})
            .build();

    @Test
    void testCreateSchedule_success() {
        try(MockedStatic<ScheduleValidator> mocked = mockStatic(ScheduleValidator.class)) {
            when(scheduleRepository.save(any())).thenReturn(mockSchedule);
            mocked.when(() -> ScheduleValidator.validateSchedule(any())).then(a -> null);
            ServiceResponse response = scheduleService.createSchedule(createScheduleRequest);
            assertEquals(Schedule.class, response.getData().getClass());
            assertEquals(MOCK_SCHEDULE_ID, ((Schedule)response.getData()).getId());
        }
    }
    @Test
    void testCreateSchedule_fail_noTime() {
        Assertions.assertThrows(InvalidScheduleException.class, () -> {
            createScheduleRequest.setTimeWindow("");
            scheduleService.createSchedule(createScheduleRequest);
        });
    }
    @Test
    void testCreateSchedule_fail_EmptyReceivers() {
        Assertions.assertThrows(InvalidScheduleException.class, () -> {
            createScheduleRequest.setReceivers(new ArrayList<>());
            scheduleService.createSchedule(createScheduleRequest);
        });
    }
    @Test
    void testCreateSchedule_fail_invalidTimeFormat() {
        Assertions.assertThrows(InvalidScheduleException.class, () -> {
            createScheduleRequest.setTimeWindow("14:18");
            scheduleService.createSchedule(createScheduleRequest);
        });
    }
    @Test
    void testDeleteSchedule_success() {
        when(scheduleRepository.findById(any())).thenReturn(Optional.of(mockSchedule));
        doNothing().when(scheduleRepository).delete(any());
        ServiceResponse response = scheduleService.deleteSchedule(MOCK_SCHEDULE_ID);
        verify(scheduleRepository, times(1)).delete(any());
        verify(scheduleRepository, times(1)).findById(any());
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus().intValue());
    }
    @Test
    void testDeleteSchedule_fail() {
        Assertions.assertThrows(EntityWithIdNotFoundException.class, () -> {
            when(scheduleRepository.findById(any())).thenReturn(Optional.empty());
            scheduleService.deleteSchedule(MOCK_SCHEDULE_ID);
        });
    }
    @Test
    void testGetUserSchedules_success() {
        when(scheduleRepository.getScheduleByUser(any())).thenReturn(new ArrayList<>(){{ add(mockSchedule); }});
        ServiceResponse response = scheduleService.getUserSchedules("bhuman@mydaytodo.com");
        verify(scheduleRepository, times(1)).getScheduleByUser(any());
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus().intValue());
    }

    @Test
    void testUpdateSchedule_success() {
        try(MockedStatic<CreateScheduleRequest> mocked = mockStatic(CreateScheduleRequest.class)) {
            when(scheduleRepository.findById(any())).thenReturn(Optional.of(mockSchedule));
            when(scheduleRepository.save(any())).thenReturn(mockSchedule);
            mocked.when(() -> CreateScheduleRequest.convert(any())).then(a -> null);
            ServiceResponse response = scheduleService.updateSchedule(MOCK_SCHEDULE_ID, createScheduleRequest);
            verify(scheduleRepository, times(1)).save(any());
            verify(scheduleRepository, times(1)).findById(any());
            Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus().intValue());
        }
    }

    @Test
    void getSchedule_success() {
        when(scheduleRepository.findById(any())).thenReturn(Optional.of(mockSchedule));
        ServiceResponse response = scheduleService.getSchedule(MOCK_SCHEDULE_ID);
        verify(scheduleRepository, times(1)).findById(any());
        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

}
