package io.github.kobylynskyi.product.graphql.config;

import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bogdankobylinsky
 */
@Configuration
public class GraphQLConfiguration {

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