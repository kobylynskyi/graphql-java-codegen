<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>

public interface ${className} {

<#list fields as field>
    ${field.type} ${field.name}(<#list field.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>) throws Exception;

</#list>
}