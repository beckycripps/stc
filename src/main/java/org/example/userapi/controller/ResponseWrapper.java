package org.example.userapi.controller;

import lombok.Data;

/**
 * ResponseWrapper<T> is a generic class. It has two private fields:
 * data: A generic type T representing the actual data that you want to wrap in this response.
 * errorMessage: A String representing any error message associated with the response.
 *
 * @param <T>
 */
@Data
public class ResponseWrapper<T> {

    private T data;
    private String errorMessage;

}
