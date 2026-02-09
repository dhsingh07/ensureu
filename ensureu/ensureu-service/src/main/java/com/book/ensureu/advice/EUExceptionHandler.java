package com.book.ensureu.advice;

import com.book.ensureu.exception.RuntimeEUException;
import com.book.ensureu.exception.unchecked.EntityNotFound;
import com.book.ensureu.response.dto.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class EUExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({EntityNotFound.class})
    public ResponseEntity<Response> handleEntityNotFoundException(EntityNotFound ex){
        Response response = Response.builder()
                .status(404)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RuntimeEUException.class})
    public ResponseEntity<Response> handleRuntimeEUException(RuntimeEUException ex){
        Response response = Response.builder()
                .status(500)
                .message(ex.getMessage())
                .build();
        return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * By overriding this method we can add custom message to any exception occurred.
     *
      */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
        }
        return new ResponseEntity(Response.builder()
                .message(ex.getMessage())
                .build(), headers, status);
    }

}
