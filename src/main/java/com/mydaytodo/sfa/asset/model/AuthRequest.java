package com.mydaytodo.sfa.asset.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AuthRequest {
    @NotNull @NotBlank
    private String username;
    @NotNull @NotBlank
    private String password;
}
