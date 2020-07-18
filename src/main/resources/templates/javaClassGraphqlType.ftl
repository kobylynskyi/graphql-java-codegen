<#if package?has_content>
package ${package};

</#if>
<#if imports??>
<#list imports as import>
import ${import}.*;
</#list>
</#if>
<#if toStringForRequest>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;
</#if>
<#if equalsAndHashCode>
import java.util.Objects;
</#if>
<#if toString>
import java.util.StringJoiner;
</#if>

<#if javaDoc?has_content>
/**
<#list javaDoc as javaDocLine>
 * ${javaDocLine}
</#list>
 */
</#if>
<#if generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
public class ${className} implements java.io.Serializable<#if implements?has_content><#list implements as interface>, ${interface}<#if interface_has_next></#if></#list></#if> {

<#if fields?has_content>
<#list fields as field>
<#if field.deprecated>
    @Deprecated
</#if>
<#list field.annotations as annotation>
    @${annotation}
</#list>
    private ${field.type} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if>;
</#list>
</#if>

    public ${className}() {
    }

<#if fields?has_content>
    public ${className}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>) {
<#list fields as field>
        this.${field.name} = ${field.name};
</#list>
    }
</#if>

<#if fields?has_content>
<#list fields as field>
<#if field.javaDoc?has_content>
    /**
<#list field.javaDoc as javaDocLine>
     * ${javaDocLine}
</#list>
     */
</#if>
<#if field.deprecated>
    @Deprecated
</#if>
    public ${field.type} get${field.name?cap_first}() {
        return ${field.name};
    }
<#if !immutableModels>
<#if field.javaDoc?has_content>
    /**
<#list field.javaDoc as javaDocLine>
     * ${javaDocLine}
</#list>
     */
</#if>
<#if field.deprecated>
    @Deprecated
</#if>
    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
    }
</#if>

</#list>
</#if>
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
<#if fields?has_content>
        return <#list fields as field>Objects.equals(${field.name}, that.${field.name})<#if field_has_next>
            && </#if></#list>;
<#else>
        return true;
</#if>
    }

    @Override
    public int hashCode() {
<#if fields?has_content>
        return Objects.hash(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>);
<#else>
        return 0;
</#if>
    }
</#if>

<#if toString>
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{ ", " }");
<#if fields?has_content>
<#list fields as field>
        if (${field.name} != null) {
<#if toStringForRequest>
            joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}));
<#else>
<#if field.type == "String">
            joiner.add("${field.originalName}: \"" + ${field.name} + "\"");
<#else>
            joiner.add("${field.originalName}: " + ${field.name});
</#if>
</#if>
        }
</#list>
</#if>
        return joiner.toString();
    }
</#if>

<#if builder>
    public static ${className}.Builder builder() {
        return new ${className}.Builder();
    }

    public static class Builder {

<#if fields?has_content>
<#list fields as field>
        private ${field.type} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if>;
</#list>
</#if>

        public Builder() {
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
<#if field.deprecated>
        @Deprecated
</#if>
        public Builder set${field.name?cap_first}(${field.type} ${field.name}) {
            this.${field.name} = ${field.name};
            return this;
        }

</#list>
</#if>

        public ${className} build() {
            return new ${className}(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>);
        }

    }
</#if>
}
