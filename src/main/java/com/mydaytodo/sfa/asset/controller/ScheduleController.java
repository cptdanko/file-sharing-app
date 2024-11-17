package com.mydaytodo.sfa.asset.controller;

import com.mydaytodo.sfa.asset.error.Validator;
import com.mydaytodo.sfa.asset.model.CreateScheduleRequest;
import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.service.ScheduleServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
@Tag(name = "Schedule Controller")
@Slf4j
public class ScheduleController {

    @Autowired
    private ScheduleServiceImpl scheduleService;

    /**
     * @param createScheduleRequestBody
     * @return
     */
    @PostMapping("/")
    public ResponseEntity<ServiceResponse> createSchedule(@RequestBody CreateScheduleRequest createScheduleRequestBody) {
        log.info("Received the request [ {} ] to create a schedule", createScheduleRequestBody.toString());
        ServiceResponse response = scheduleService.createSchedule(createScheduleRequestBody);
        log.info("Finished creating a response ----------- [ {} ]", response.toString());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> getSchedule(@PathVariable("id") String id) {
        log.info("Received request for {}", id);
        ServiceResponse response = scheduleService.getSchedule(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
    @GetMapping("/by/{username}")
    public ResponseEntity<ServiceResponse> getScheduleByUsername(@PathVariable("username") String username) {
        log.info("Received request to get schedules for {}", username);
        Validator.validateUsernameAndToken(username);
        ServiceResponse response = scheduleService.getUserSchedules(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping(value = "/{id}" , consumes = {"application/json"})
    public ResponseEntity<ServiceResponse> updateSchedule(@PathVariable("id") String id, @RequestBody CreateScheduleRequest createScheduleRequest) {
        log.info("Received request to update schedule with {} id and [ {} ] body", id, createScheduleRequest.toString());
        ServiceResponse response = scheduleService.updateSchedule(id, createScheduleRequest);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResponse> deleteSchedule(@PathVariable("id") String id) {
        ServiceResponse response = scheduleService.deleteSchedule(id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
}
