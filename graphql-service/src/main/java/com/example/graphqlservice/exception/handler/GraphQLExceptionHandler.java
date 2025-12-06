package com.example.graphqlservice.exception.handler;

import com.example.graphqlservice.exception.GraphQLOrderRequestRuntimeException;
import com.example.graphqlservice.exception.GraphQLProductRequestRuntimeException;
import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Map;

@ControllerAdvice
public class GraphQLExceptionHandler {

    @GraphQlExceptionHandler(GraphQLOrderRequestRuntimeException.class)
    public GraphQLError handleException(GraphQLOrderRequestRuntimeException e) {
        return GraphQLError.newError()
                .message(e.getMessage())
                .extensions(Map.of("statusCode", e.getStatusCode()))
                .build();
    }

    @GraphQlExceptionHandler(GraphQLProductRequestRuntimeException.class)
    public GraphQLError handleException(GraphQLProductRequestRuntimeException e) {
        return GraphQLError.newError()
                .message(e.getMessage())
                .extensions(Map.of("statusCode", e.getStatusCode()))
                .build();
    }

}
