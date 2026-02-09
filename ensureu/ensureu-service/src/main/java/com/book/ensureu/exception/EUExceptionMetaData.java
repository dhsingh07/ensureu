package com.book.ensureu.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class EUExceptionMetaData {
    private HttpStatus httpStatus;
    private String message;
    private String debugMessage;
    private int code;

    public EUExceptionMetaData(String msg){
        this.message = msg;
        this.httpStatus = HttpStatus.BAD_REQUEST;
        this.debugMessage = msg;
        this.code = HttpStatus.BAD_REQUEST.value();
    }
}
