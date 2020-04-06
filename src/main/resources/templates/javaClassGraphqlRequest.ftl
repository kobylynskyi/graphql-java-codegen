<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>

public class ${className} implements com.kobylynskyi.graphql.codegen.model.request.GraphQLOperationRequest {

    private static final OperationDefinition.Operation OPERATION_TYPE = OperationDefinition.Operation.${operationType};
    private static final String OPERATION_NAME = "${operationName}";

    private Map<String, Object> input = new LinkedHashMap<>();

    public ${className}() {
    }

<#list fields as field>
    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.input.put("${field.name}", ${field.name});
    }

</#list>
    @Override
    public OperationDefinition.Operation getOperationType() {
        return OPERATION_TYPE;
    }

    @Override
    public String getOperationName() {
        return OPERATION_NAME;
    }

    @Override
    public Map<String, Object> getInput() {
        return input;
    }

<#if equalsAndHashCode>
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ${className} that = (${className}) obj;
        return Objects.equals(getOperationType(), that.getOperationType()) &&
                   Objects.equals(getOperationName(), that.getOperationName()) &&
                   Objects.equals(input, that.input);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOperationType(), getOperationName(), input);
    }
</#if>
<#if toString>
    @Override
    public String toString() {
        return Objects.toString(input);
    }
</#if>
}
