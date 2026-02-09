package com.book.ensureu.exception;

import lombok.Data;

@Data
public class GenericException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String message;

    public GenericException() {
        super();
    }

    public GenericException(String message) {
        super(message);
    }

}
