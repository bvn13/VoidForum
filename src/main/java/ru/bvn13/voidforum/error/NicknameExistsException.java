package ru.bvn13.voidforum.error;


import lombok.Getter;

public class NicknameExistsException extends Exception {
    @Getter
    private String message;

    public NicknameExistsException(){}

    public NicknameExistsException(String message){
        this.message = message;
    }
}
