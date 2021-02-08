<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
import java.util.StringJoiner
<#if javaDoc?has_content>
/**
<#list javaDoc as javaDocLine>
 * ${javaDocLine}
</#list>
 */
</#if>
<#if generatedAnnotation && generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = ["com.kobylynskyi.graphql.codegen.GraphQLCodegen"],
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
<#if !fields?has_content>
class ${className}() : GraphQLParametrizedInput {
    override fun toString(): String = "()"
}
<#else>
data class ${className}(
<#if fields?has_content>
<#list fields as field>
    <#if field.deprecated?has_content>
        @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
    </#if>
    <#list field.annotations as annotation>@get:${annotation}
    </#list>val ${field.name}: ${field.type}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if><#if field_has_next>,</#if>
</#list>
</#if>
) : GraphQLParametrizedInput {

    override fun toString(): String {
        val joiner = StringJoiner(", ", "( ", " )")
        <#list fields as field>
        <#if field.type?ends_with("?")>
        if (${field.name} != null) {
            joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}))
        }
        <#else>
        joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}))
        </#if>
        </#list>
        return joiner.toString()
    }
}
</#if>
