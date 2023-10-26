package org.example.userapi.conroller;

import lombok.Data;

@Data
public class ResponseWrapper<T> {

    private T data;
    private String errorMessage;

}
