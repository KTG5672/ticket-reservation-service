package io.github.ktg.ticketing.api.exception;

import io.github.ktg.ticketing.common.exception.BusinessException;
import io.github.ktg.ticketing.common.exception.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionTestController {

    @GetMapping("/exception")
    public void exception() throws Exception {
        throw new Exception("Exception Now!");
    }

    @GetMapping("/business-exception")
    public void businessException() {
        throw new BusinessException(TestErrorCode.TEST_ERROR);
    }

    enum TestErrorCode implements ErrorCode {

        TEST_ERROR;

        @Override
        public String code() {
            return name();
        }

        @Override
        public String message() {
            return "Test Error!";
        }
    }

}
