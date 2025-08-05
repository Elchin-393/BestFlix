package com.bestflix.movie.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
 public class UserMoviesNotFoundException extends BaseException{

    public UserMoviesNotFoundException(){
        super(new ErrorMessage("Relevant Movies Not Found", MessageType.NO_MOVIES_EXIST) , HttpStatus.NOT_FOUND);
    }
}
