package io.github.kobylynskyi.order.graphql.config;

import graphql.GraphQLError;
import graphql.kickstart.spring.error.ThrowableGraphQLError;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class GraphQLConfiguration {

    @ExceptionHandler(Exception.class)
    public GraphQLError exception(Exception e) {
        return new ThrowableGraphQLError(e);
    }

    @Bean
    public GraphQLScalarType extendedScalarsDateTime() {
        return ExtendedScalars.DateTime;
    }

    @Bean
    public GraphQLScalarType extendedScalarsDate() {
        return ExtendedScalars.Date;
    }

    @Bean
    public GraphQLScalarType extendedScalarsBigDecimal() {
        return ExtendedScalars.GraphQLBigDecimal;
    }

}
