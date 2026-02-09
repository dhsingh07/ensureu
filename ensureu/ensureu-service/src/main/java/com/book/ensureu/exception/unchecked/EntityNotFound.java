package com.book.ensureu.exception.unchecked;

import com.book.ensureu.exception.RuntimeEUException;

public class EntityNotFound extends RuntimeEUException {

    public EntityNotFound(String message){
        super(message);
    }
}
