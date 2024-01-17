package com.kobylynskyi.graphql.codegen.generators;

/**
 * Type of the FreeMarker template
 */
public enum FreeMarkerTemplateType {

    TYPE,
    ENUM,
    UNION,
    REQUEST,
    RESPONSE,
    INTERFACE,
    OPERATIONS,
    PARAMETRIZED_INPUT,
    RESPONSE_PROJECTION,
    JACKSON_TYPE_ID_RESOLVER

}
