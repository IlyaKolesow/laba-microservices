package com.example.graphqlservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class GraphQLOrderRequestRuntimeException extends GraphQLRuntimeException {

    private final HttpStatusCode statusCode;

    public GraphQLOrderRequestRuntimeException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}
