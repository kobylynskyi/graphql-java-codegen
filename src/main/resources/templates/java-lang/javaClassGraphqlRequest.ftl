<#if package?has_content>
package ${package};

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
<#if toString || equalsAndHashCode>
import java.util.Objects;
</#if>
import java.util.Set;

<#if javaDoc?has_content>
/**
<#list javaDoc as javaDocLine>
 * ${javaDocLine}
</#list>
 */
</#if>
<#if generatedAnnotation && generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
public class ${className} implements GraphQLOperationRequest {

    public static final String OPERATION_NAME = "${operationName}";
    public static final GraphQLOperation OPERATION_TYPE = GraphQLOperation.${operationType};

    private String alias;
    private final Map<String, Object> input = new LinkedHashMap<>();
    private final Set<String> useObjectMapperForInputSerialization = new HashSet<>();

    public ${className}() {
    }

    public ${className}(String alias) {
        this.alias = alias;
    }

<#if fields?has_content>
<#list fields as field>
<#if field.javaDoc?has_content>
    /**
<#list field.javaDoc as javaDocLine>
     * ${javaDocLine}
</#list>
     */
</#if>
<#if field.deprecated?has_content>
    @${field.deprecated.annotation}
</#if>
    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.input.put("${field.originalName}", ${field.name});
        <#if field.serializeUsingObjectMapper>
        this.useObjectMapperForInputSerialization.add("${field.originalName}");
        </#if>
    }

</#list>
</#if>
    @Override
    public GraphQLOperation getOperationType() {
        return OPERATION_TYPE;
    }

    @Override
    public String getOperationName() {
        return OPERATION_NAME;
    }

    @Override
    public String getAlias() {
        return alias != null ? alias : OPERATION_NAME;
    }

    @Override
    public Map<String, Object> getInput() {
        return input;
    }

    @Override
    public Set<String> getUseObjectMapperForInputSerialization() {
        return useObjectMapperForInputSerialization;
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
    public static ${className}.Builder builder() {
        return new ${className}.Builder();
    }

    public static class Builder {

        private String $alias;
<#if fields?has_content>
<#list fields as field>
        private ${field.type} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if>;
</#list>
</#if>

        public Builder() {
        }

        public Builder alias(String alias) {
            this.$alias = alias;
            return this;
        }

<#if fields?has_content>
<#list fields as field>
<#if field.javaDoc?has_content>
        /**
<#list field.javaDoc as javaDocLine>
         * ${javaDocLine}
</#list>
         */
</#if>
<#if field.deprecated?has_content>
        @${field.deprecated.annotation}
</#if>
        public Builder set${field.name?cap_first}(${field.type} ${field.name}) {
            this.${field.name} = ${field.name};
            return this;
        }

</#list>
</#if>

        public ${className} build() {
            ${className} obj = new ${className}($alias);
<#if fields?has_content>
<#list fields as field>
            obj.set${field.name?cap_first}(${field.name});
</#list>
</#if>
            return obj;
        }

    }
</#if>
}
