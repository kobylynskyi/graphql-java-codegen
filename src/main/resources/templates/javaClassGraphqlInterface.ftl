<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>

<#if javaDoc?has_content>
/**
<#list javaDoc as javaDocLine>
 * ${javaDocLine}
</#list>
 */
</#if>
public interface ${className} <#if implements?has_content>implements <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if>{

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
<#list field.annotations as annotation>
    @${annotation}
</#list>
    ${field.type} get${field.name?cap_first}();

</#list>
}