package io.github.ktg.ticketing.app.user.web.dto;

import io.github.ktg.ticketing.domain.user.port.in.SignInResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {

    private String accessToken;

    public static SignInResponse from(SignInResult signInResult) {
        return new SignInResponse(signInResult.accessToken());
    }

}
