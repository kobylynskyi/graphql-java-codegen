package com.kobylynskyi.graphql.codegen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Freemarker-understandable format of method parameter and field definition
 *
 * @author kobylynskyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDefinition {

    private String type;
    private String name;
    private String defaultValue;
    private List<String> annotations = new ArrayList<>();
}
