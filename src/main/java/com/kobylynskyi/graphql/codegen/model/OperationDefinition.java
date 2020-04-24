package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Freemarker-understandable format of operation (Query/Mutation/Subscription)
 *
 * @author kobylynskyi
 */
@Data
public class OperationDefinition {

    private String name;
    private String type;
    private List<String> annotations = new ArrayList<>();
    private List<ParameterDefinition> parameters = new ArrayList<>();
    private List<String> javaDoc = new ArrayList<>();

}
