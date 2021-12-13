package com.cs.ganda.controller.advices;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorControllerAdvice {

    public static final String CREDENTIALS_INVALID = "Vos identifiants sont invalides ou votre compte n'est pas actif";
    public static final String DATA_INVALID = "Les données que vous avez saisi sont invalides";
    private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String unauthorized(final Throwable throwable, final Model model) {
        logger.error("Exception during execution of SpringSecurity application", throwable);
        String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }
    
    @ExceptionHandler(ClassNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String pageNotFound(final Throwable throwable, final Model model) {
        logger.error("Exception during execution of SpringSecurity application", throwable);
        String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
        model.addAttribute("errorMessage", errorMessage);
        return "404";
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String nullPointerException(final Throwable throwable, final Model model) {
        logger.error("Exception during execution of SpringSecurity application", throwable);
        String errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }


    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Map<String, String> HttpMessageNotReadableException(final Throwable throwable) {
        log.error("errorMessage", throwable);
        return Collections.singletonMap("message", DATA_INVALID);
    }

    @ExceptionHandler({LockedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public @ResponseBody
    Map<String, String> accountLockedException(final Throwable throwable) {
        log.error("errorMessage", throwable);
        return Collections.singletonMap("message", CREDENTIALS_INVALID);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Map<String, String> illegalArgumentException(final Throwable throwable) {
        log.error("errorMessage", throwable);
        String errorMessage = (throwable != null ? throwable.getMessage() : CREDENTIALS_INVALID);
        return Collections.singletonMap("message", errorMessage);
    }

    @ExceptionHandler({InvalidDataAccessApiUsageException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody
    Map<String, String> stripeException(final Throwable throwable) {
        log.error("errorMessage", throwable);
        String errorMessage = (throwable != null ? throwable.getMessage() : CREDENTIALS_INVALID);
        return Collections.singletonMap("message", errorMessage);
    }
}
