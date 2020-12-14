<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
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
        @Deprecated("this is deprecated in GraphQL")
    </#if>
    <#list field.annotations as annotation>@get:${annotation}
    </#list>val ${field.name}: ${field.type}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if><#if field_has_next>,</#if>
</#list>
</#if>
) : GraphQLParametrizedInput
</#if>