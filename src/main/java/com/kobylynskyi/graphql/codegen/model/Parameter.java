package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

/**
 * Freemarker-understandable format of method parameter
 *
 * @author kobylynskyi
 */
@Data
public class Parameter {

    private String type;
    private String name;

}
