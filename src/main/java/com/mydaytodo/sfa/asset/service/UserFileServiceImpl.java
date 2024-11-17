package com.mydaytodo.sfa.asset.service;

import com.mydaytodo.sfa.asset.model.ServiceResponse;
import com.mydaytodo.sfa.asset.model.FileWithSchedule;
import com.mydaytodo.sfa.asset.model.db.Schedule;
import com.mydaytodo.sfa.asset.repository.S3Repository;
import com.mydaytodo.sfa.asset.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserFileServiceImpl {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private S3Repository s3Repository;

    public ServiceResponse getUserFileSchedules(String username) {
        // add some validations

        List<String> userUploadedFiles = s3Repository.filesByUser(username);
        List<Schedule> schedules = scheduleRepository.getScheduleByUser(username);
        List<FileWithSchedule> userFileList = new ArrayList<>();
        userUploadedFiles.forEach(file -> {
            FileWithSchedule uf = FileWithSchedule.builder()
                    .filename(file)
                    .build();
            log.info("Got the list of files size, {}",userUploadedFiles.size());
            schedules.stream()
                    .filter(schedule -> schedule.getFilename() != null
                            && schedule.getFilename().equalsIgnoreCase(file))
                    .findFirst()
                    .ifPresent(uf::setSchedule);
            log.info("After adding the file schedule, {}", uf.getSchedule());
            userFileList.add(uf);
        });
        return ServiceResponse.builder()
                .data(userFileList)
                .status(HttpStatus.OK.value())
                .build();
    }
}
