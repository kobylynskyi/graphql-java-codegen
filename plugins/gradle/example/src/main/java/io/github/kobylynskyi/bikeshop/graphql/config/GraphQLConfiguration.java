package io.github.kobylynskyi.bikeshop.graphql.config;

import graphql.GraphQL;
import graphql.execution.AsyncExecutionStrategy;
import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author bogdankobylinsky
 */
@Configuration
public class GraphQLConfiguration {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    @Bean
    public GraphQL graphQL(GraphQLSchema graphQLSchema) {
        return GraphQL.newGraphQL(graphQLSchema)
                .queryExecutionStrategy(new AsyncExecutionStrategy())
                .mutationExecutionStrategy(new AsyncExecutionStrategy())
                .build();
    }

    @Bean
    public GraphQLScalarType dateGraphQLScalarType() {
        return GraphQLScalarType.newScalar()
                .name("DateTime")
                .coercing(new Coercing() {
                    @Override
                    public Object serialize(Object o) throws CoercingSerializeException {
                        return DATE_FORMAT.format((Date) o);
                    }

                    @Override
                    public Object parseValue(Object o) throws CoercingParseValueException {
                        return serialize(o);
                    }

                    @Override
                    public Object parseLiteral(Object o) throws CoercingParseLiteralException {
                        try {
                            return DATE_FORMAT.parse(((StringValue) o).getValue());
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                }).build();
    }

}