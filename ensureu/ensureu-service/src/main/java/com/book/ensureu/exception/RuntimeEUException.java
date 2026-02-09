package com.book.ensureu.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class RuntimeEUException extends RuntimeException{

    protected EUExceptionMetaData euExceptionMetaData;

    public RuntimeEUException(){
        super();
    }

    public RuntimeEUException(String message){
        this.euExceptionMetaData = new EUExceptionMetaData(message);
    }

}
