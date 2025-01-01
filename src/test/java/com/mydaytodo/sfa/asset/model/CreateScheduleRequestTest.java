package com.mydaytodo.sfa.asset.model;

import com.mydaytodo.sfa.asset.model.db.Schedule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

public class CreateScheduleRequestTest {
    private final String username = "cptdanko@mydaytodo.com";
    private final String name = "Bhuman Soni";

    private final CreateScheduleRequest sampleRequest = CreateScheduleRequest.builder()
            .isRecurring(false)
            .senderName(name)
            .senderEmail(username)
            .receivers(new ArrayList<>(){{add("bhuman@mydaytodo.com");}})
            .sendDate(new Date())
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
        Date modSendDate = new Date();
        String modSenderMail = "admin@mydaytodo.com";
        sampleRequest.setSenderName(modSenderName);
        sampleRequest.setSendDate(modSendDate);
        sampleRequest.setSenderEmail(modSenderMail);
        CreateScheduleRequest.updateValues(schedule, sampleRequest);
        Assertions.assertEquals(modSenderName, schedule.getSender());
        Assertions.assertEquals(modSenderMail, schedule.getUsername());
        Assertions.assertEquals(modSendDate, schedule.getSendDate());
    }
}
