package ru.bvn13.voidforum.error;

import lombok.Getter;

public class EmailExistsException extends Exception {
    @Getter
    private String message;

    public EmailExistsException(){}

    public EmailExistsException(String message){
        this.message = message;
    }
}
