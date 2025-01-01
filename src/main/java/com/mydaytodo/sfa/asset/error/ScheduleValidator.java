package com.mydaytodo.sfa.asset.error;

import com.mydaytodo.sfa.asset.model.CreateScheduleRequest;

import java.util.regex.Pattern;

public class ScheduleValidator {

    public static void validateSchedule(CreateScheduleRequest createScheduleRequest) {
        if (createScheduleRequest.getReceivers().isEmpty()) {
            throw new InvalidScheduleException("Please supply email address to send this too");
        }
        if(createScheduleRequest.getSendDate() == null) {
            throw new InvalidScheduleException("Please supply a valid time window");
        }
        /* String regex = "\\d+-\\d+";
        Pattern pattern = Pattern.compile(regex);
        if(!pattern.matcher(createScheduleRequest.getTimeWindow()).matches()) {
            throw new InvalidScheduleException("Please supply a valid time window");
        }*/
    }
}
