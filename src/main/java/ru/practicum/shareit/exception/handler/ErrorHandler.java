package ru.practicum.shareit.exception.handler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFoundExceptionHandler(Exception e) {
        log.error("Сущность не найдена, {}", e.getMessage());
        return ErrorResponse.fromMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse throwableHandler(Throwable t) {
        t.printStackTrace();
        return ErrorResponse.fromMessage(t.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse dataIntegrityViolationExceptionHandler(DataIntegrityViolationException e) {
        return ErrorResponse.fromMessage(e.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BadRequestException.class,
            MethodArgumentNotValidException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequestExceptionHandler(Exception e) {
        return ErrorResponse.fromMessage(e.getMessage());
    }

    @Getter
    @Setter
    public static class ErrorResponse {
        private String error;

        private static ErrorResponse fromMessage(String str) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setError(str);
            return errorResponse;
        }
    }
}
