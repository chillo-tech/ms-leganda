package com.cs.ganda.controller.advices;

import com.cs.ganda.dto.ApiErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDTO noHandlerFoundException(RuntimeException ex) {
        log.error("{}", ex);
        return new ApiErrorDTO(1000, ex.getMessage());
    }
}
