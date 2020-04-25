package com.kobylynskyi.graphql.codegen.model;

import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Freemarker-understandable format of method parameter and field definition
 *
 * @author kobylynskyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDefinition {

    public static final ParameterDefinition DATA_FETCHING_ENVIRONMENT = new ParameterDefinition(
            DataFetchingEnvironment.class.getName(), "env", null, emptyList(), emptyList(), false);

    private String type;
    private String name;
    private String defaultValue;
    private List<String> annotations = new ArrayList<>();
    private List<String> javaDoc = new ArrayList<>();
    private boolean deprecated;

}
