package com.mydaytodo.sfa.asset.utilities;

import java.time.LocalTime;

public class DateTimeService {
    public static String getTimeWindow() {
        LocalTime now = LocalTime.now();
        String nextHour = now.getHour() == 23 ? "0": now.getHour() + 1+"";
        return now.getHour() + "-"+nextHour;
    }
}
