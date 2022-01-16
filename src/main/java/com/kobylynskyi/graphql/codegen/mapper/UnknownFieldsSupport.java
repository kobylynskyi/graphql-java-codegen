package com.kobylynskyi.graphql.codegen.mapper;

import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.ParameterDefinition;

import java.util.Arrays;
import java.util.Optional;

/**
 * Utility interface that provides convenience methods to handle unknown fields during the marshaling
 * and unmarshalling of a JSON document
 *
 * @author aldib
 */
public interface UnknownFieldsSupport {

    /**
     * Creates an instance of {@link ParameterDefinition} that can be used to generate
     * a field of type {@link java.util.Map} to store unknown fields during the marshaling
     * and unmarshalling of a JSON document
     *
     * @param mappingContext The context of the mapping process.
     * @return If {@link MappingContext#isSupportUnknownFields()} is true, it returns a monad containing
     *         the instance of {@link ParameterDefinition}. {@link Optional#empty()} otherwise.
     */
    default Optional<ParameterDefinition> createUnknownFields(MappingContext mappingContext) {
        if (mappingContext.isSupportUnknownFields()) {
            ParameterDefinition unknownFields = new ParameterDefinition();
            unknownFields.setName(mappingContext.getUnknownFieldsPropertyName());
            unknownFields.setOriginalName(mappingContext.getUnknownFieldsPropertyName());
            unknownFields.setType("java.util.Map<String, Object>");
            unknownFields.setAnnotations(Arrays.asList(
                    "com.fasterxml.jackson.annotation.JsonAnyGetter",
                    "com.fasterxml.jackson.annotation.JsonAnySetter"
            ));
            return Optional.of(unknownFields);
        }
        return Optional.empty();
    }
}
