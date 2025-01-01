package com.mydaytodo.sfa.asset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydaytodo.sfa.asset.TestUtils;
import com.mydaytodo.sfa.asset.error.Validator;
import com.mydaytodo.sfa.asset.model.CreateScheduleRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import com.mydaytodo.sfa.asset.service.ScheduleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ScheduleControllerTest {
    @Mock
    private ScheduleServiceImpl scheduleService;

    @InjectMocks
    private ScheduleController scheduleController;

    private final String MOCK_SCHEDULE_ID = "SCH_123";
    private final String BASE_URL = "/api/schedule";
    private ServiceResponse updateSchedule(String userId, String scheduleId) {
        return null;
    }
    JacksonTester<CreateScheduleRequest> createScheduleRequestJacksonTester;
    JacksonTester<Schedule> scheduleJacksonTester;
    private MockMvc mockMvc;
    private final CreateScheduleRequest createScheduleRequest = CreateScheduleRequest.builder()
            .isRecurring(false)
            .senderName("bhuman")
            .senderEmail("bhuman@mydaytodo.com")
            // .timeWindow("12-13")
            .receivers(new ArrayList<>() {{ add("bhuman@mydaytodo.com"); add("bhuman.soni@gmail.com"); }})
            .build();

    @BeforeEach
    void populate() {
        JacksonTester.initFields(this, new ObjectMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();
    }

    @Test
    void testCreateSchedule_success() throws Exception {
        when(scheduleService.createSchedule(any())).thenReturn(TestUtils.createdResponse);
        String url = BASE_URL + "/";
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createScheduleRequestJacksonTester.write(createScheduleRequest).getJson()))
                .andExpect(status().isCreated());
    }
    @Test
    void testGetSchedule() throws Exception {
        String url = BASE_URL + "/" + MOCK_SCHEDULE_ID;
        when(scheduleService.getSchedule(any())).thenReturn(TestUtils.successResponse);
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void testGetScheduleBy() throws Exception {
        String url = BASE_URL + "/by/bhuman@mydaytodo.com";
        try(MockedStatic<Validator> mocked = mockStatic(Validator.class)) {
            when(scheduleService.getUserSchedules(any())).thenReturn(TestUtils.successResponse);
            mocked.when(() -> Validator.validateUsernameAndToken(any())).then(a -> null);
            mockMvc.perform(get(url)
                    .header("Authorization" ,"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrZWxuYWNhQGdtYWlsLmNvbSIsImlhdCI6MTcyNDM4MjcwOCwiZXhwIjoxNzI0NDIxNjAwfQ.MKUQ5uXAd2Iy_kXKcw-n-ezyuam7mjxwtsTFZyFX0Zc")
                    .param("username", "kelnaca@gmail.com")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk());
        }
    }
    @Test
    void testUpdateSchedule() throws Exception {
        String url = BASE_URL + "/"+ MOCK_SCHEDULE_ID;
        when(scheduleService.updateSchedule(any(), any())).thenReturn(TestUtils.noContentResponse);
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON)
                        .content(createScheduleRequestJacksonTester.write(createScheduleRequest).getJson()))
                .andExpect(status().isNoContent());
    }
    @Test
    void testDeleteSchedule() throws Exception {
        String url = BASE_URL + "/" + MOCK_SCHEDULE_ID;
        when(scheduleService.deleteSchedule(any())).thenReturn(TestUtils.noContentResponse);
        mockMvc.perform(delete(url))
                .andExpect(status().isNoContent());
    }
}
