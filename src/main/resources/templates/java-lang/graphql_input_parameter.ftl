<#if package?has_content>
package ${package};
</#if>
<#list imports as import>
import ${import}.*;
</#list>


<#if generatedAnnotation && generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
date = "${generatedInfo.getDateTime()}"
)
</#if>
/**
 * A wrapper class for GraphQL input parameters that supports values, null, or undefined.
 * This class is designed to handle various types using generics.
 *
 * @param <T> The type of the input parameter.
 */
public class GraphQLInputParameter<T> {

    private T value;
    private boolean isDefined;

    /**
     * Private constructor to create a GraphQLInputParameter instance with a value and definition status.
     *
     * @param value     The value of the input parameter.
     * @param isDefined A boolean flag indicating whether the value is defined.
     */
    private GraphQLInputParameter(T value, boolean isDefined) {
        this.value = value;
        this.isDefined = isDefined;
    }

    /**
     * Creates a GraphQLInputParameter instance with a specified value.
     *
     * @param <T>   The type of the input parameter.
     * @param value The value of the input parameter.
     * @return A GraphQLInputParameter instance with the specified value.
     */
    public static <T> GraphQLInputParameter<T> withValue(T value) {
        return new GraphQLInputParameter<>(value, true);
    }

    /**
     * Creates a GraphQLInputParameter instance with a null value.
     *
     * @param <T> The type of the input parameter.
     * @return A GraphQLInputParameter instance with a null value.
     */
    public static <T> GraphQLInputParameter<T> withNull() {
        return new GraphQLInputParameter<>(null, true);
    }

    /**
     * Creates a GraphQLInputParameter instance representing an undefined value.
     *
     * @param <T> The type of the input parameter.
     * @return A GraphQLInputParameter instance representing an undefined value.
     */
    public static <T> GraphQLInputParameter<T> undefined() {
        return new GraphQLInputParameter<>(null, false);
    }

    /**
     * Checks whether the input parameter has a defined value.
     *
     * @return {@code true} if the value is defined, {@code false} otherwise.
     */
    public boolean isDefined() {
        return isDefined;
    }

    /**
     * Checks whether the input parameter does not have a defined value.
     *
     * @return {@code false} if the value is defined, {@code true} otherwise.
     */
    public boolean isUndefined() {
        return !isDefined;
    }

    /**
     * Gets the value of the input parameter. Throws an IllegalStateException if the value is undefined.
     *
     * @return The value of the input parameter.
     * @throws IllegalStateException If the value is undefined.
     */
    public T getValue() {
        if (!isDefined) {
            throw new IllegalStateException("Value is undefined");
        }
        return value;
    }

    /**
     * Gets the value of the input parameter or default value.
     * @param <T>           The type of the input parameter.
     * @param defaultValue  The value to return if the input parameter is undefined.
     * @return The value of the input parameter, or a default value if undefined.
     */
    public T getValueOrDefault(T defaultValue) {
        if (!isDefined) {
            return defaultValue;
        }
        return value;
    }
}