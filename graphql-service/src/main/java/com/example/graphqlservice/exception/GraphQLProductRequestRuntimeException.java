package com.example.graphqlservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class GraphQLProductRequestRuntimeException extends GraphQLRuntimeException {

    private final HttpStatusCode statusCode;

    public GraphQLProductRequestRuntimeException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}