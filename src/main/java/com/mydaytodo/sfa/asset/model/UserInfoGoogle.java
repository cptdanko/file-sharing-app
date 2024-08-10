package com.mydaytodo.sfa.asset.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoGoogle {

    @Email(message = "Email should be valid")
    private String email;

    private String name;
    // for now just from their Google profile
    private String pictureLink;

    public static CreateUserRequest convertToCreateUserRequest(UserInfoGoogle userInfoGoogle) {
        return CreateUserRequest.builder()
                .isSocialLoginGoogle(true)
                .name(userInfoGoogle.getName())
                .username(userInfoGoogle.getEmail())
                .profilePicLink(userInfoGoogle.getPictureLink())
                .build();
    }
}
