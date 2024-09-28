package com.mydaytodo.sfa.asset.model;

import com.mydaytodo.sfa.asset.model.db.Schedule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CreateScheduleRequestTest {
    private final String username = "cptdanko@mydaytodo.com";
    private final String name = "Bhuman Soni";

    private CreateScheduleRequest sampleRequest = CreateScheduleRequest.builder()
            .isRecurring(false)
            .senderName(name)
            .senderEmail(username)
            .receivers(new ArrayList<>(){{add("bhuman@mydaytodo.com");}})
            .timeWindow("9-11")
            .build();

    @Test
    public void testConvert() {
        Schedule schedule = CreateScheduleRequest.convert(sampleRequest);
        Assertions.assertEquals(name, schedule.getSender());
        Assertions.assertEquals(username, schedule.getUsername());

    }
    @Test
    public void testUpdateValues() {
        Schedule schedule = CreateScheduleRequest.convert(sampleRequest);
        String modSenderName = "Captain Danko";
        String modTimeWin = "16-18";
        String modSenderMail = "admin@mydaytodo.com";
        sampleRequest.setSenderName(modSenderName);
        sampleRequest.setTimeWindow(modTimeWin);
        sampleRequest.setSenderEmail(modSenderMail);
        CreateScheduleRequest.updateValues(schedule, sampleRequest);
        Assertions.assertEquals(modSenderName, schedule.getSender());
        Assertions.assertEquals(modSenderMail, schedule.getUsername());
        Assertions.assertEquals(modTimeWin, schedule.getTimeWindow());
    }
}
