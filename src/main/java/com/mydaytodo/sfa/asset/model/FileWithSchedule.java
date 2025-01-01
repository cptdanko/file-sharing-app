package com.mydaytodo.sfa.asset.model;

import com.mydaytodo.sfa.asset.model.db.Schedule;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class FileWithSchedule {
    private String filename;
    private Schedule schedule;
}
