package com.mydaytodo.sfa.asset.model;

import com.mydaytodo.sfa.asset.model.db.Schedule;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@ToString
@Builder
public class CreateScheduleRequest {
    private String id;
    private String senderName;
    private String senderEmail;
    private List<String> receivers; //email address of those receiving
    private String timeWindow;
    private boolean isRecurring = false;

    public static Schedule convert(CreateScheduleRequest createScheduleRequest) {
        Schedule schedule = new Schedule();
        schedule.setRecurring(createScheduleRequest.isRecurring());
        schedule.setReceivers(createScheduleRequest.getReceivers());
        schedule.setTimeWindow(createScheduleRequest.getTimeWindow());
        schedule.setReceivers(createScheduleRequest.getReceivers());
        schedule.setSender(createScheduleRequest.getSenderName());
        schedule.setUsername(createScheduleRequest.getSenderEmail());
        return schedule;
    }
    public static void updateValues(Schedule schedule, CreateScheduleRequest createScheduleRequest) {
        if(!schedule.getUsername().equals(createScheduleRequest.getSenderEmail())) {
            schedule.setUsername(createScheduleRequest.getSenderEmail());
        }
        if(!schedule.getTimeWindow().equals(createScheduleRequest.getTimeWindow())) {
            schedule.setTimeWindow(createScheduleRequest.getTimeWindow());
        }
        if(!schedule.getSender().equals(createScheduleRequest.getSenderName())) {
            schedule.setSender(createScheduleRequest.getSenderName());
        }
        // for now, just copy the receivers as it is from DTO to the schedule object
        schedule.setReceivers(createScheduleRequest.getReceivers());
    }
}
