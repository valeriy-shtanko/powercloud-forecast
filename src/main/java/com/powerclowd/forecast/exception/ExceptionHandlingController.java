package com.powerclowd.forecast.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;

import javax.servlet.http.HttpServletRequest;


@ControllerAdvice
public class ExceptionHandlingController {

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ExceptionResponse allExceptions(final Exception ex, final HttpServletRequest request) {

        return ExceptionResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Internal Server Error")
                .url(request.getRequestURI())
                .details(ex.getMessage())
                .build();
    }

    @ExceptionHandler({RestClientException.class})
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ResponseBody
    public ExceptionResponse externalApiExceptions(final RestClientException ex, final HttpServletRequest request) {

        return ExceptionResponse.builder()
                .status(HttpStatus.FAILED_DEPENDENCY.value())
                .title("Failed to request external API")
                .url(request.getRequestURI())
                .details(ex.getMessage())
                .build();
    }

    @ExceptionHandler({InternalErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ExceptionResponse internalError(final InternalErrorException ex, final HttpServletRequest request) {

        return ExceptionResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Internal Server Error")
                .url(request.getRequestURI())
                .details(ex.getMessage())
                .build();
    }

    @ExceptionHandler({BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ExceptionResponse badRequest(final BadRequestException ex, final HttpServletRequest request) {

        return ExceptionResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .title("Invalid request data")
                .url(request.getRequestURI())
                .details(ex.getMessage())
                .build();
    }
}
