package com.kobylynskyi.graphql.codegen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Freemarker-understandable format of enum value definition
 *
 * @author kobylynskyi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnumValueDefinition {

    private String value;
    private List<String> javaDoc = new ArrayList<>();
}
