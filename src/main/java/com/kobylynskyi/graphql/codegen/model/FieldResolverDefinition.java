package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Freemarker-understandable format of parametrized field.
 * <p>
 * Example schema:
 * <code>
 * type Person {
 * name: String!
 * height(unit: Unit): Int!
 * }
 * </code>
 * <p>
 * Here:
 * <p>
 * name = "height"
 * type = "Integer"
 * parameters = [Person person, Unit unit, DataFetchingEnvironment env]
 * <p>
 * So that resulting interface will have:
 * <code>
 * Integer height(Person person, Unit unit, DataFetchingEnvironment env);
 * </code>
 *
 * @author kobylynskyi
 */
@Data
public class FieldResolverDefinition {

    private String name;
    private String type;
    private List<ParameterDefinition> parameters = new ArrayList<>();

}
