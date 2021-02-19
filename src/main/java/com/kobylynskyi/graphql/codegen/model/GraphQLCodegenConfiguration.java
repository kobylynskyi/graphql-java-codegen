package com.kobylynskyi.graphql.codegen.model;

import java.util.List;
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
     * <ul>
     *   <li>Map of (GraphqlObjectName.fieldName) to (JavaType)</li>
     *   <li>Map of (GraphqlType) to (JavaType)</li>
     * </ul>
     * <p>
     * E.g.:
     * <ul>
     *   <li>{@code DateTime --- String}</li>
     *   <li>{@code Price.amount --- java.math.BigDecimal}</li>
     * </ul>
     *
     * @return mappings from GraphqlType to JavaType
     */
    Map<String, String> getCustomTypesMapping();

    /**
     * Can be used to supply custom annotations (serializers) for scalars.
     * <p>
     * Supports:
     * <ul>
     *   <li>Map of (GraphqlObjectName.fieldName) to (JavaAnnotation)</li>
     *   <li>Map of (GraphqlType) to (JavaAnnotation)</li>
     * </ul>
     * <p>
     * E.g.:
     * {@code EpochMillis --- @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.example.json.EpochMillisScalarDeserializer.class)}
     *
     * @return mappings from GraphqlType to JavaAnnotation
     */
    Map<String, List<String>> getCustomAnnotationsMapping();

    /**
     * Map GraphQL directives to Java annotations.
     * <p>
     * Directive fields can be used in annotations via: {{directiveFieldName}}
     * <p>
     * Example:
     * <p>
     * schema: directive <code>@auth (roles: [String])</code>
     * <p>
     * {@code directiveAnnotationsMapping: auth --- @org.springframework.security.access.annotation.Secured({{roles}})}
     *
     * @return mappings from GraphQL directives to Java annotations.
     */
    Map<String, List<String>> getDirectiveAnnotationsMapping();

    /**
     * Specifies whether api classes should be generated.
     *
     * @return <b>true</b> is API interfaces should be generated.
     */
    Boolean getGenerateApis();

    /**
     * Specifies whether model classes should be generated for Query/Subscription/Mutation.
     *
     * @return <b>true</b> is model classes (POJOs) should be generated for GraphQL root types.
     */
    Boolean getGenerateModelsForRootTypes();

    /**
     * Specifies the strategy of generating root api interface.
     *
     * @return strategy of generating root api interface.
     */
    ApiRootInterfaceStrategy getApiRootInterfaceStrategy();

    /**
     * Specifies the strategy of generating api interfaces.
     *
     * @return strategy of generating api interfaces.
     */
    ApiInterfaceStrategy getApiInterfaceStrategy();

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
     * @return The suffix for GraphQL model classes.
     */
    String getModelNameSuffix();

    /**
     * Sets prefix strategy for GraphQL api classes (query, mutation, subscription).
     *
     * @return Prefix strategy for GraphQL api classes.
     */
    ApiNamePrefixStrategy getApiNamePrefixStrategy();

    /**
     * Sets the prefix for GraphQL api classes (query, mutation, subscription).
     *
     * @return The prefix for GraphQL api classes.
     */
    String getApiNamePrefix();

    /**
     * Sets the suffix for GraphQL api classes (query, mutation, subscription).
     *
     * @return The suffix for GraphQL api classes.
     */
    String getApiNameSuffix();

    /**
     * Annotation for mandatory (NonNull) fields. Can be null/empty.
     *
     * @return Annotation for mandatory (NonNull) fields
     */
    String getModelValidationAnnotation();

    /**
     * Return type for api methods (query / subscription)
     * For example: <i>reactor.core.publisher.Mono</i>
     *
     * @return Return type for api methods (query / subscription)
     */
    String getApiReturnType();

    /**
     * Return type for api methods (query / subscription) that return list values
     * For example: <i>reactor.core.publisher.Flux</i>
     *
     * @return Return type for api methods (query / subscription) that return list values
     */
    String getApiReturnListType();

    /**
     * Return type for subscription methods.
     * For example: <i>org.reactivestreams.Publisher</i>, <i>io.reactivex.Observable</i>, etc.
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
     * Specifies whether generated model classes should be immutable.
     *
     * @return <b>true</b> if generated model classes should be immutable.
     */
    Boolean getGenerateImmutableModels();

    /**
     * Specifies whether generated model classes should have toString method defined.
     *
     * @return <b>true</b> if generated model classes should have toString method defined.
     */
    Boolean getGenerateToString();

    /**
     * If true, then generate separate `Resolver` interface for parametrized fields.
     * If false, then add field to the type definition and ignore field parameters.
     *
     * @return <b>true</b> if separate `Resolver` interface for parametrized fields should be generated.
     * <b>false</b> if field should be added to the type definition and field parameters should be ignored.
     */
    Boolean getGenerateParameterizedFieldsResolvers();

    /**
     * Sets the prefix for GraphQL type resolver classes.
     *
     * @return The prefix for GraphQL type resolver classes.
     */
    String getTypeResolverPrefix();

    /**
     * Sets the suffix for GraphQL type resolver classes.
     *
     * @return The suffix for GraphQL type resolver classes.
     */
    String getTypeResolverSuffix();

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
     * Whether signatures of API interface methods should have <code>throws Exception</code>.
     *
     * @return <b>true</b> if API interfaces methods signature should contain <code>throws Exception</code>.
     */
    Boolean getGenerateApisWithThrowsException();

    /**
     * Specifies whether generated classes should be annotated with @Generated
     *
     * @return <b>true</b> if classes should be annotated with @Generated
     */
    Boolean getAddGeneratedAnnotation();

    /**
     * Relay-related configurations.
     *
     * @return Relay-related configurations.
     */
    RelayConfig getRelayConfig();

    /**
     * Fields that require Resolvers.
     * <p>
     * Values should be defined here in format: TypeName, TypeName.fieldName, @directive
     * <p>
     * If just type is specified, then all fields of this type will have resolvers.
     * <p>
     * E.g.:
     * <ul>
     *   <li>{@code Person}</li>
     *   <li>{@code Person.friends}</li>
     *   <li>{@code @customResolver}</li>
     * </ul>
     *
     * @return Set of types and fields that should have Resolver interfaces.
     */
    Set<String> getFieldsWithResolvers();

    /**
     * Fields that DO NOT require Resolvers.
     * <p>
     * Values should be defined here in format: TypeName, TypeName.fieldName, @directive
     * <p>
     * If just type is specified, then all fields of this type will NOT have resolvers.
     * <p>
     * Can be used in conjunction with {@code generateExtensionFieldsResolvers}
     * <p>
     * E.g.:
     * <ul>
     *   <li>{@code Person}</li>
     *   <li>{@code Person.friends}</li>
     *   <li>{@code @customResolver}</li>
     * </ul>
     *
     * @return Set of types and fields that should NOT have Resolver interfaces.
     */
    Set<String> getFieldsWithoutResolvers();

    /**
     * Specifies whether return types of generated API interface should be wrapped into <code>java.util.Optional</code>
     *
     * @return <b>true</b> if return types should be wrapped into <code>java.util.Optional</code>
     */
    Boolean getUseOptionalForNullableReturnTypes();

    /**
     * Specifies whether client-side classes should be generated for each query, mutation and subscription.
     * This includes: `Request` class (contains input data) and `ResponseProjection` class (contains response fields).
     *
     * @return <b>true</b> if client-side classes should be generated (`Request`, `Response` and `ResponseProjection`)
     */
    Boolean getGenerateClient();

    /**
     * The suffix for `Request` classes.
     *
     * @return The suffix for `Request` classes.
     */
    String getRequestSuffix();

    /**
     * The suffix for `Response` classes.
     *
     * @return The suffix for `Response` classes.
     */
    String getResponseSuffix();

    /**
     * The suffix for `ResponseProjection` classes.
     *
     * @return The suffix for `ResponseProjection` classes.
     */
    String getResponseProjectionSuffix();

    /**
     * The suffix for `ParametrizedInput` classes.
     *
     * @return The suffix for `ParametrizedInput` classes.
     */
    String getParametrizedInputSuffix();

    /**
     * Interface that will be added as "extend" to all generated api Query interfaces.
     *
     * @return Parent interface of all GraphQL Query types.
     */
    String getQueryResolverParentInterface();

    /**
     * Interface that will be added as "extend" to all generated api Mutation interfaces.
     *
     * @return Parent interface of all GraphQL Mutation types.
     */
    String getMutationResolverParentInterface();

    /**
     * Interface that will be added as "extend" to all generated api Subscription interfaces.
     *
     * @return Parent interface of all GraphQL Subscription types.
     */
    String getSubscriptionResolverParentInterface();

    /**
     * Interface that will be added as "extend" to all generated TypeResolver interfaces.
     *
     * @return Parent interface of all Resolvers of GraphQL types.
     */
    String getResolverParentInterface();

    /**
     * Limit depth when `all$` invoke which has subProjections
     *
     * @return limit depth when the projection is constructed automatically
     */
    Integer getResponseProjectionMaxDepth();

    /**
     * Fields that require serialization using
     * {@link com.fasterxml.jackson.databind.ObjectMapper#writeValueAsString(Object)}
     * <p>
     * Values should be defined here in format: <i>GraphqlObjectName.fieldName</i> or <i>GraphqlTypeName</i>
     * <p>
     * If just type is specified, then all fields of this type will be serialized using ObjectMapper.
     * <p>
     * E.g.:
     * <ul>
     *   <li>{@code Person.createdDateTime}</li>
     *   <li>{@code ZonedDateTime}</li>
     * </ul>
     *
     * @return Set of types and fields that should be serialized using
     * {@link com.fasterxml.jackson.databind.ObjectMapper#writeValueAsString(Object)}
     */
    Set<String> getUseObjectMapperForRequestSerialization();

    /**
     * Generate code with lang
     *
     * @return GeneratedLanguage.SCALA or GeneratedLanguage.JAVA
     */
    GeneratedLanguage getGeneratedLanguage();

    /**
     * Specifies whether generate public model classes.
     *
     * @return <b>false</b> generate data class in kotlin and case class in scala
     */
    Boolean isGenerateModelOpenClasses();

}
