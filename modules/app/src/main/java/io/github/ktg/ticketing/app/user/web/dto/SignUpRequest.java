package io.github.ktg.ticketing.app.user.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이메일은 필수 입력 입니다.")
    private String email;

    @NotBlank(message = "패스워드는 필수 입력 입니다.")
    private String password;

}
