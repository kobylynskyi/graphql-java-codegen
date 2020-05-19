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
public interface ${className}<#if implements?has_content> extends <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if> {

<#list operations as operation>
<#if operation.javaDoc?has_content>
    /**
<#list operation.javaDoc as javaDocLine>
     * ${javaDocLine}
</#list>
     */
</#if>
<#if operation.deprecated>
    @Deprecated
</#if>
<#list operation.annotations as annotation>
    @${annotation}
</#list>
    ${operation.type} ${operation.name}(<#list operation.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>) throws Exception;

</#list>
}