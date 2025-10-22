package io.github.ktg.ticketing.app.api.exception;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MvcExceptionRequest {

    @NotBlank(message = "message is not blank")
    private String message;

}
