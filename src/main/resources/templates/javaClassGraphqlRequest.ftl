<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>

<#if javaDoc?has_content>
/**
<#list javaDoc as javaDocLine>
 * ${javaDocLine}
</#list>
 */
</#if>
public class ${className} implements GraphQLOperationRequest {

    private static final GraphQLOperation OPERATION_TYPE = GraphQLOperation.${operationType};
    private static final String OPERATION_NAME = "${operationName}";

    private Map<String, Object> input = new LinkedHashMap<>();

    public ${className}() {
    }

<#list fields as field>
<#if field.javaDoc?has_content>
    /**
<#list field.javaDoc as javaDocLine>
     * ${javaDocLine}
</#list>
     */
</#if>
    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.input.put("${field.name}", ${field.name});
    }

</#list>
    @Override
    public GraphQLOperation getOperationType() {
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

<#if builder>
    public static class Builder {

<#list fields as field>
        private ${field.type} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if>;
</#list>

        public Builder() {
        }

<#list fields as field>
<#if field.javaDoc?has_content>
        /**
<#list field.javaDoc as javaDocLine>
         * ${javaDocLine}
</#list>
         */
</#if>
        public Builder set${field.name?cap_first}(${field.type} ${field.name}) {
            this.${field.name} = ${field.name};
            return this;
        }

</#list>

        public ${className} build() {
            ${className} obj = new ${className}();
<#list fields as field>
            obj.set${field.name?cap_first}(${field.name});
</#list>
            return obj;
        }

    }
</#if>
}
