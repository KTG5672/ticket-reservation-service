package io.github.ktg.ticketing.app.api.exception;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MvcExceptionTestController {

    @PostMapping("/argument-valid")
    public String argumentNotValid(@Valid @RequestBody MvcExceptionRequest request) {
        return request.getMessage();
    }

    @GetMapping("/request-parameter")
    public String requestParameterNotValid(@RequestParam Integer param) {
        return param.toString();
    }

}
