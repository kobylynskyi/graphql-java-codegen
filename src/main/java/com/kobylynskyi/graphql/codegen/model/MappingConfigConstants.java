package com.kobylynskyi.graphql.codegen.model;

public class MappingConfigConstants {

    public static final String DEFAULT_VALIDATION_ANNOTATION = "javax.validation.constraints.NotNull";
    public static final String PARENT_INTERFACE_TYPE_PLACEHOLDER = "{{TYPE}}";
    public static final boolean DEFAULT_GENERATE_APIS = true;
    public static final String DEFAULT_GENERATE_APIS_STRING = "true";
    public static final boolean DEFAULT_BUILDER = true;
    public static final String DEFAULT_BUILDER_STRING = "true";
    public static final boolean DEFAULT_EQUALS_AND_HASHCODE = false;
    public static final String DEFAULT_EQUALS_AND_HASHCODE_STRING = "false";
    public static final boolean DEFAULT_GENERATE_IMMUTABLE_MODELS = false;
    public static final String DEFAULT_GENERATE_IMMUTABLE_MODELS_STRING = "false";
    public static final boolean DEFAULT_TO_STRING = false;
    public static final String DEFAULT_TO_STRING_STRING = "false";
    public static final boolean DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS = true;
    public static final String DEFAULT_GENERATE_PARAMETERIZED_FIELDS_RESOLVERS_STRING = "true";
    public static final boolean DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS = false;
    public static final String DEFAULT_GENERATE_EXTENSION_FIELDS_RESOLVERS_STRING = "false";
    public static final boolean DEFAULT_GENERATE_DATA_FETCHING_ENV = false;
    public static final String DEFAULT_GENERATE_DATA_FETCHING_ENV_STRING = "false";
    public static final boolean DEFAULT_GENERATE_MODELS_FOR_ROOT_TYPES = false;
    public static final String DEFAULT_GENERATE_MODELS_FOR_ROOT_TYPES_STRING = "false";
    public static final boolean DEFAULT_GENERATE_APIS_WITH_THROWS_EXCEPTION = true;
    public static final String DEFAULT_GENERATE_APIS_WITH_THROWS_EXCEPTION_STRING = "true";
    public static final boolean DEFAULT_ADD_GENERATED_ANNOTATION = true;
    public static final String DEFAULT_ADD_GENERATED_ANNOTATION_STRING = "true";
    public static final boolean DEFAULT_USE_OPTIONAL_FOR_NULLABLE_RETURN_TYPES = false;
    public static final String DEFAULT_USE_OPTIONAL_FOR_NULLABLE_RETURN_TYPES_STRING = "false";
    public static final ApiNamePrefixStrategy DEFAULT_API_NAME_PREFIX_STRATEGY = ApiNamePrefixStrategy.CONSTANT;
    public static final String DEFAULT_API_NAME_PREFIX_STRATEGY_STRING = "CONSTANT";
    public static final ApiRootInterfaceStrategy DEFAULT_API_ROOT_INTERFACE_STRATEGY = ApiRootInterfaceStrategy.SINGLE_INTERFACE;
    public static final String DEFAULT_API_ROOT_INTERFACE_STRATEGY_STRING = "SINGLE_INTERFACE";
    public static final ApiInterfaceStrategy DEFAULT_API_INTERFACE_STRATEGY = ApiInterfaceStrategy.INTERFACE_PER_OPERATION;
    public static final String DEFAULT_API_INTERFACE_STRATEGY_STRING = "INTERFACE_PER_OPERATION";
    public static final boolean DEFAULT_GENERATE_CLIENT = false;
    public static final String DEFAULT_GENERATE_CLIENT_STRING = "false";

    public static final String DEFAULT_RESOLVER_SUFFIX = "Resolver";
    public static final String DEFAULT_REQUEST_SUFFIX = "Request";
    public static final String DEFAULT_RESPONSE_SUFFIX = "Response";
    public static final String DEFAULT_RESPONSE_PROJECTION_SUFFIX = "ResponseProjection";
    public static final String DEFAULT_PARAMETRIZED_INPUT_SUFFIX = "ParametrizedInput";

    public static final String DEFAULT_RESPONSE_PROJECTION_MAX_DEPTH_STRING = "3";
    public static final int DEFAULT_RESPONSE_PROJECTION_MAX_DEPTH = 3;

    public static final GeneratedLanguage DEFAULT_GENERATED_LANGUAGE = GeneratedLanguage.JAVA;
    public static final String DEFAULT_GENERATED_LANGUAGE_STRING = "JAVA";

    // This library is the only one that supports Scala well and is generally used.
    // There is no need to consider the possibility of switching.
    public static final String DEFAULT_SERIALIZATION_LIBRARY = "JACKSON";

    //It only support in kotlin and scala.
    public static final boolean DEFAULT_GENERATE_MODEL_OPEN_CLASSES = false;
    public static final String DEFAULT_GENERATE_MODEL_OPEN_CLASSES_STRING = "false";


    private MappingConfigConstants() {
    }
}
