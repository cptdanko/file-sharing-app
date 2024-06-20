package com.mydaytodo.sfa.asset.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    @Email
    private String to;
    @Email
    private String[] cc;
    @Email
    private String[] bcc;
    private String subject;
    private String body;
    private String[] filesToAttach;
}
