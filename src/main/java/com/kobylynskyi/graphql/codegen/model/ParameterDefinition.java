package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Freemarker-understandable format of method parameter and field definition
 *
 * @author kobylynskyi
 */
@Data
public class ParameterDefinition {

    private String type;
    private String name;
    private List<String> annotations = new ArrayList<>();

}
