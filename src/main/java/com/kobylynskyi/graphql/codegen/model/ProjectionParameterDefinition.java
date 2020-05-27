package com.kobylynskyi.graphql.codegen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Freemarker-understandable format of parameter user in ResponseProjection
 *
 * @author kobylynskyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectionParameterDefinition {

    private String type;
    private String name;
    private boolean deprecated;
    private String parametrizedInputClassName;

}
