package com.bestflix.movie.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException{

    private final HttpStatus status;
    private final ErrorMessage errorMessage;

    public BaseException(ErrorMessage errorMessage, HttpStatus status){
        super(errorMessage.getFullMessage());
        this.status = status;
        this.errorMessage = errorMessage;

    }

}

