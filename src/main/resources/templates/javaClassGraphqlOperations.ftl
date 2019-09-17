<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>

public interface ${className} {

<#list operations as operation>
<#list operation.annotations as annotation>
    @${annotation}
</#list>
    ${operation.type} ${operation.name}(<#list operation.parameters as param>${param.type} ${param.name}<#if param_has_next>, </#if></#list>) throws Exception;

</#list>
}