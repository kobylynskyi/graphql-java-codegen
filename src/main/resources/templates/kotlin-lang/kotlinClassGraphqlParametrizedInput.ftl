<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.kotlin.KotlinGraphQLTypeMapper"]>
<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import java.util.StringJoiner
<#if equalsAndHashCode>
import java.util.Objects
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
    value = ["com.kobylynskyi.graphql.codegen.GraphQLCodegen"],
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
<#if !fields?has_content>
class ${className}() : GraphQLParametrizedInput
<#else>
data class ${className}(
<#if fields?has_content>
<#list fields as field>
    <#if field.deprecated>
        @java.lang.Deprecated
    </#if>
    <#list field.annotations as annotation>@get:${annotation}
    </#list>val ${field.name}: ${field.type}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if><#if field_has_next>,</#if>
</#list>
</#if>
) : GraphQLParametrizedInput {

    override fun toString(): String {
        val joiner = StringJoiner(", ", "(", ")")
<#if fields?has_content>
<#list fields as field>
        <#if field.type?ends_with("?")>
            <#if MapperUtil.isKotlinPrimitive(field.type)>
        joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}))
            <#else>
        if (${field.name} != null) {
            joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}))
        }
             </#if>
        </#if>
</#list>
</#if>
        return joiner.toString()
    }

}
</#if>