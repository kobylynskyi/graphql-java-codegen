package com.kobylynskyi.graphql.codegen.model;

import java.util.Map;
import java.util.Set;

/**
 * Defines all properties that should be parsed in the plugin.
 */
public interface GraphQLCodegenConfiguration {

    /**
     * Can be used to supply custom mappings for scalars.
     * <p>
     * Supports:
     * * Map of (GraphqlObjectName.fieldName) to (JavaType)
     * * Map of (GraphqlType) to (JavaType)
     * <p>
     * e.g.: DateTime --- String
     * e.g.: Price.amount --- java.math.BigDecimal
     *
     * @return mappings from GraphqlType to JavaType
     */
    Map<String, String> getCustomTypesMapping();

    /**
     * Can be used to supply custom annotations (serializers) for scalars.
     * <p>
     * Supports:
     * * Map of (GraphqlObjectName.fieldName) to (JavaAnnotation)
     * * Map of (GraphqlType) to (JavaAnnotation)
     * <p>
     * e.g.: EpochMillis --- com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.EpochMillisScalarDeserializer.class)
     *
     * @return mappings from GraphqlType to JavaAnnotation
     */
    Map<String, String> getCustomAnnotationsMapping();

    /**
     * Specifies whether api classes should be generated.
     *
     * @return <b>true</b> is API interfaces should be generated.
     */
    Boolean getGenerateApis();

    /**
     * Java package for generated classes.
     *
     * @return Java package for generated classes.
     */
    String getPackageName();

    /**
     * Java package for generated api classes (Query, Mutation, Subscription).
     *
     * @return Java package for generated api classes.
     */
    String getApiPackageName();

    /**
     * Java package for generated model classes (type, input, interface, enum, union).
     *
     * @return Java package for generated model classes.
     */
    String getModelPackageName();

    /**
     * Sets the prefix for GraphQL model classes (type, input, interface, enum, union).
     *
     * @return The prefix for GraphQL model classes.
     */
    String getModelNamePrefix();

    /**
     * Sets the suffix for GraphQL model classes (type, input, interface, enum, union).
     *
     * @return The prefix for GraphQL model classes.
     */
    String getModelNameSuffix();

    /**
     * Annotation for mandatory (NonNull) fields. Can be null/empty.
     *
     * @return Annotation for mandatory (NonNull) fields
     */
    String getModelValidationAnnotation();

    /**
     * Return type for subscription methods.
     * For example: `org.reactivestreams.Publisher`, `io.reactivex.Observable`, etc.
     *
     * @return Return type for subscription methods
     */
    String getSubscriptionReturnType();

    /**
     * Specifies whether generated model classes should have builder.
     *
     * @return <b>true</b> if generated model classes should have builder.
     */
    Boolean getGenerateBuilder();

    /**
     * Specifies whether generated model classes should have equals and hashCode methods defined.
     *
     * @return <b>true</b> if generated model classes should have equals and hashCode methods defined.
     */
    Boolean getGenerateEqualsAndHashCode();

    /**
     * Specifies whether generated model classes should have toString method defined.
     *
     * @return <b>true</b> if generated model classes should have toString method defined.
     */
    Boolean getGenerateToString();

    /**
     * If true, then wrap type into `java.util.concurrent.CompletableFuture` or `subscriptionReturnType`
     *
     * @return <b>true</b> if types need to be wrapped into `java.util.concurrent.CompletableFuture` or `subscriptionReturnType`
     */
    Boolean getGenerateAsyncApi();

    /**
     * If true, then generate separate `Resolver` interface for parametrized fields.
     * If false, then add field to the type definition and ignore field parameters.
     *
     * @return <b>true</b> if separate `Resolver` interface for parametrized fields should be generated.
     * <b>false</b> if field should be added to the type definition and field parameters should be ignored.
     */
    Boolean getGenerateParameterizedFieldsResolvers();

    /**
     * Whether all fields in extensions (<code>extend type</code> and <code>extend interface</code>) should be present
     * in Resolver interface instead of the type class itself.
     *
     * @return <b>true</b> if all fields in extensions (<code>extend type</code> and <code>extend interface</code>)
     * should be present in Resolver interface instead of the type class itself.
     */
    Boolean getGenerateExtensionFieldsResolvers();

    /**
     * If true, then <b>graphql.schema.DataFetchingEnvironment env</b> will be added as a last argument
     * to all methods of root type resolvers and field resolvers.
     *
     * @return <b>true</b> if <b>graphql.schema.DataFetchingEnvironment env</b> should be added as a last argument
     * in all API interfaces methods.
     */
    Boolean getGenerateDataFetchingEnvironmentArgumentInApis();

    /**
     * Fields that require Resolvers should be defined here in format: TypeName.fieldName
     * If just type is specified, then all fields of this type will have resolvers
     * <p>
     * E.g.: "Person.friends"
     * E.g.: "Person"
     *
     * @return Set of types and fields that should have Resolver interfaces.
     */
    Set<String> getFieldsWithResolvers();

    /**
     * Fields that DO NOT require Resolvers should be defined here in format: TypeName.fieldName
     * If just type is specified, then all fields of this type will NOT have resolvers
     * Can be used in conjunction with "generateExtensionFieldsResolvers"
     * <p>
     * E.g.: "Person.friends"
     * E.g.: "Person"
     *
     * @return Set of types and fields that should NOT have Resolver interfaces.
     */
    Set<String> getFieldsWithoutResolvers();

    /**
     * Specifies whether client-side classes should be generated for each query, mutation and subscription.
     * This includes: `Request` class (contains input data) and `ResponseProjection` class (contains response fields).
     *
     * @return <b>true</b> if client-side classes should be generated (`Request` and `ResponseProjection`)
     */
    Boolean getGenerateRequests();

    /**
     * The suffix for `Request` classes.
     *
     * @return The suffix for `Request` classes.
     */
    String getRequestSuffix();

    /**
     * The suffix for `ResponseProjection` classes.
     *
     * @return The suffix for `ResponseProjection` classes.
     */
    String getResponseProjectionSuffix();
}
