package com.book.ensureu.exception.unchecked;

import com.book.ensureu.exception.RuntimeEUException;

public class InvalidRequestException extends RuntimeEUException {

    public InvalidRequestException(String msg){
        super(msg);
    }
}
