package com.bestflix.movie.exception;

import org.springframework.http.HttpStatus;

public class MovieNotFoundException extends BaseException{

    public MovieNotFoundException(){
        super(new ErrorMessage("Movie Not Found", MessageType.NO_MOVIES_EXIST) , HttpStatus.NOT_FOUND);
    }
}
