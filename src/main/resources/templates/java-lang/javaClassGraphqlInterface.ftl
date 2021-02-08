<#if package?has_content>
package ${package};

</#if>
<#if imports??>
<#list imports as import>
import ${import}.*;
</#list>
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
public interface ${className} <#if implements?has_content>extends <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if>{

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
<#list field.annotations as annotation>
    @${annotation}
</#list>
    ${field.type} get${field.name?cap_first}();

</#list>
</#if>
}
