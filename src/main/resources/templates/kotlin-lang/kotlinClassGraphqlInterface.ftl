<#if package?has_content>
package ${package}

</#if>
<#if imports??>
    <#list imports as import>
import ${import}.*
    </#list>
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
interface ${className} <#if implements?has_content> : <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if>{

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
    @java.lang.Deprecated
    </#if>
    <#list field.annotations as annotation>
    @${annotation}
    </#list>
    <#if !immutableModels>var <#else>val </#if>${field.name}: ${field.type}

    </#list>
</#if>
}