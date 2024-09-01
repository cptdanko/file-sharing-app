package com.mydaytodo.sfa.asset.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleUserInfoRequest {

    @Email(message = "Email should be valid")
    @NotNull @NotBlank
    private String email;

    @NotBlank @NotNull
    private String name;
    // for now just from their Google profile
    @NotBlank @NotNull
    private String pictureLink;

    public static CreateUserRequest convertToCreateUserRequest(GoogleUserInfoRequest googleUserInfoRequest) {
        return CreateUserRequest.builder()
                .isSocialLoginGoogle(true)
                .name(googleUserInfoRequest.getName())
                .username(googleUserInfoRequest.getEmail())
                .profilePicLink(googleUserInfoRequest.getPictureLink())
                .build();
    }
}
