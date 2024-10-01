package com.mydaytodo.sfa.asset.repository;

import com.mydaytodo.sfa.asset.model.db.Schedule;

import java.util.List;

public interface CustomScheduleRepository<T, Z> {
    List<Schedule> getScheduleByUser(String username);
    void updateScheduleIsSent(String scheduleId, boolean isSent);
}
