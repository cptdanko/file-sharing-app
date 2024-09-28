package com.mydaytodo.sfa.asset.repository;

import com.mydaytodo.sfa.asset.model.db.Schedule;
import org.springframework.data.repository.CrudRepository;

public interface ScheduleRepository extends CrudRepository<Schedule, String>, CustomScheduleRepository<Schedule, String> {
}
