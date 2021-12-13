package com.sky.fodmap.service.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private String fieldNotFound;
    private String fieldValue;

    public NotFoundException(String fieldNotFound, String fieldValue){
        this.fieldNotFound = fieldNotFound;
        this.fieldValue = fieldValue;
    }
}
