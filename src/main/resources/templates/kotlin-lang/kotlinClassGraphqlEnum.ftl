<#if package?has_content>
package ${package}

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
    value = ["com.kobylynskyi.graphql.codegen.GraphQLCodegen"],
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
enum class ${className}(val graphqlName: String)<#if implements?has_content> : <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if> {

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
    @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
</#if>
    ${field.javaName}("${field.graphqlName}")<#if field_has_next>,
<#else></#if>
</#list>

</#if>
}
