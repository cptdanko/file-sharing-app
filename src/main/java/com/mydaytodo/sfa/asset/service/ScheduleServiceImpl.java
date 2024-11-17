package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.constants.KeyStart;
import com.mydaytodo.sfa.asset.error.EntityWithIdNotFoundException;
import com.mydaytodo.sfa.asset.error.ScheduleValidator;
import com.mydaytodo.sfa.asset.jobs.EmailSchedulerJob;
import com.mydaytodo.sfa.asset.model.CreateScheduleRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import com.mydaytodo.sfa.asset.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ScheduleServiceImpl {
    /*@Autowired
    private ScheduleRepositoryImplLecyga scheduleRepository;*/
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EmailSchedulerJob emailSchedulerJob;

    public ServiceResponse createSchedule(CreateScheduleRequest createScheduleRequest) {
        // validate the schedule
        ScheduleValidator.validateSchedule(createScheduleRequest);
        // create the schedule - store it in the database
        // trigger the schedule
        Schedule schedule = CreateScheduleRequest.convert(createScheduleRequest);
        schedule.setIsSent(false);
        schedule.setId(KeyStart.SCHEDULE_KEY + System.currentTimeMillis());
        log.info("About to save the schedule [ {} ]", schedule.toString());
        Schedule dbSchedule = scheduleRepository.save(schedule);
        log.info("Successfully created the schedule");
        emailSchedulerJob.scheduleSendMail(schedule);
        // create or start the schedule
        return ServiceResponse.builder()
                .status(HttpStatus.CREATED.value())
                .data(dbSchedule)
                .message("")
                .build();
    }
    public ServiceResponse deleteSchedule(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new EntityWithIdNotFoundException(
                                String.format("Entity with id %s not found", id)));
        scheduleRepository.delete(schedule);
        return ServiceResponse.builder()
                .status(HttpStatus.NO_CONTENT.value())
                .data(null)
                .build();
    }
    // retrieve a list of schedules by user
    public ServiceResponse getUserSchedules(String username) {
        // validate username
        log.info("about to get schedules");
        List<Schedule> userSchedules = scheduleRepository.getScheduleByUser(username);
        log.info(Arrays.toString(userSchedules.toArray()));
        return ServiceResponse.builder()
                .data(userSchedules)
                .status(HttpStatus.OK.value())
                .build();
    }

    public ServiceResponse updateSchedule(String id, CreateScheduleRequest scheduleDto) {
        // this schedule parameters should be validated here
        log.info("Got request to update the schedule");
        Schedule schedule = scheduleRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityWithIdNotFoundException(
                                        String.format("Entity with id %s not found", id)));
        log.info("About to convert [ {} ] to [ {} ] ", schedule.toString(), scheduleDto.toString());
        // map the DTO to an actual object
        CreateScheduleRequest.updateValues(schedule, scheduleDto);
        scheduleRepository.save(schedule);
        log.info("saved the [ {} ] schedule object", schedule.toString());
        return ServiceResponse.builder()
                .data(null)
                .status(HttpStatus.NO_CONTENT.value())
                .message("")
                .build();

    }
    public ServiceResponse getSchedule(String id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() ->
                        new EntityWithIdNotFoundException(
                                String.format("Schedule with id %s not found", id)));

        return ServiceResponse.builder()
                .data(schedule)
                .message("")
                .status(HttpStatus.OK.value())
                .build();

    }
}
