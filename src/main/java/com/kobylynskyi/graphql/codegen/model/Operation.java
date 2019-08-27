package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

import java.util.List;

/**
 * Freemarker-understandable format of operation (Query/Mutation/Subscription)
 *
 * @author kobylynskyi
 */
@Data
public class Operation {

    private String name;
    private String type;
    private List<Parameter> parameters;

}
