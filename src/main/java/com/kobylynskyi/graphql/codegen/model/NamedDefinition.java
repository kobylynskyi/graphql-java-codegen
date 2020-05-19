package com.kobylynskyi.graphql.codegen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NamedDefinition {

    private String name;
    private boolean isInterface;

}
