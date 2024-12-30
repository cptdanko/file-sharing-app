package com.mydaytodo.sfa.asset.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
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
    private List<String> receivers; // email address of those receiving
    private boolean isRecurring = false;
    private String filename;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date sendDate;


    public static Schedule convert(CreateScheduleRequest createScheduleRequest) {
        Schedule schedule = new Schedule();
        schedule.setRecurring(createScheduleRequest.isRecurring());
        schedule.setReceivers(createScheduleRequest.getReceivers());
        schedule.setReceivers(createScheduleRequest.getReceivers());
        schedule.setSender(createScheduleRequest.getSenderName());
        schedule.setUsername(createScheduleRequest.getSenderEmail());
        schedule.setFilename(createScheduleRequest.getFilename());
        schedule.setSendDate(createScheduleRequest.getSendDate());
        return schedule;
    }
    public static void updateValues(Schedule schedule, CreateScheduleRequest createScheduleRequest) {
        if(!schedule.getUsername().equals(createScheduleRequest.getSenderEmail())) {
            schedule.setUsername(createScheduleRequest.getSenderEmail());
        }
        if(!schedule.getSendDate().equals(createScheduleRequest.getSendDate())) {
            schedule.setSendDate(createScheduleRequest.getSendDate());
        }
        if(!schedule.getSender().equals(createScheduleRequest.getSenderName())) {
            schedule.setSender(createScheduleRequest.getSenderName());
        }
        // for now, just copy the receivers as it is from DTO to the schedule object
        schedule.setReceivers(createScheduleRequest.getReceivers());
    }
}
