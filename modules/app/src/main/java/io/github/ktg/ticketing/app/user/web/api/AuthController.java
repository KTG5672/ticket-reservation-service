package io.github.ktg.ticketing.app.user.web.api;

import io.github.ktg.ticketing.app.user.web.dto.SignUpRequest;
import io.github.ktg.ticketing.app.user.web.dto.SignUpResponse;
import io.github.ktg.ticketing.common.api.ApiErrorResponse;
import io.github.ktg.ticketing.domain.user.port.in.SignUpCommand;
import io.github.ktg.ticketing.domain.user.port.in.SignUpUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "로그인/회원가입 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final SignUpUseCase signUpUseCase;

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패 (이메일/패스워드 형식 오류, 이메일 중복)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class)
            )
        )
    })
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(
        @Valid @RequestBody SignUpRequest request) {
        SignUpCommand signUpCommand = new SignUpCommand(request.getEmail(), request.getPassword());
        String userId = signUpUseCase.signUp(signUpCommand);
        return ResponseEntity.ok(new SignUpResponse(userId));
    }

}
