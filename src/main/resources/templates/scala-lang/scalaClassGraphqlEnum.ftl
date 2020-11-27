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
<#if generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = Array("com.kobylynskyi.graphql.codegen.GraphQLCodegen"),
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
object ${className} extends Enumeration<#if implements?has_content> with<#if fields?has_content><#list implements as interface> ${interface}<#if interface_has_next> with </#if></#list></#if></#if> {

    type ${className} = Value

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
    val ${field.javaName}: Value = Value("${field.graphqlName}")
</#list>
</#if>

}