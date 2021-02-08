<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.java.JavaGraphQLTypeMapper"]>
<#if package?has_content>
package ${package};

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer;
import java.util.StringJoiner;
<#if equalsAndHashCode>
import java.util.Objects;
</#if>

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
public class ${className} implements GraphQLParametrizedInput {

<#if fields?has_content>
<#list fields as field>
<#if field.deprecated?has_content>
    @${field.deprecated.annotation}
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
<#if field.deprecated?has_content>
    @${field.deprecated.annotation}
</#if>
    public ${className} ${field.name}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
        return this;
    }

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

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "(", ")");
<#if fields?has_content>
<#list fields as field>
<#if MapperUtil.isJavaPrimitive(field.type)>
        joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}));
<#else>
        if (${field.name} != null) {
            joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}));
        }
</#if>
</#list>
</#if>
        return joiner.toString();
    }

}
