package ru.practicum.shareit.exception.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    @Mock
    private Logger log;

    @InjectMocks
    ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNotFoundExceptionHandler() {
        ErrorHandler.ErrorResponse response = errorHandler.notFoundExceptionHandler(new NotFoundException("Error"));
        Assertions.assertEquals(response.getError(), "Error");
    }

    @Test
    void testThrowableHandler() {
        ErrorHandler.ErrorResponse response = errorHandler.throwableHandler(new NotFoundException("Error"));
        Assertions.assertEquals(response.getError(), "Error");
    }

    @Test
    void testDataIntegrityViolationExceptionHandler() {
        ErrorHandler.ErrorResponse response = errorHandler
                .dataIntegrityViolationExceptionHandler(new DataIntegrityViolationException("Error"));
        Assertions.assertEquals(response.getError(), "Error");
    }

    @Test
    void testBadRequestExceptionHandler() {
        ErrorHandler.ErrorResponse response = errorHandler.badRequestExceptionHandler(new BadRequestException("Error"));
        Assertions.assertEquals(response.getError(), "Error");
    }

}