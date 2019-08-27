<#if package?has_content>
package ${package};

</#if>
public enum ${className} {

<#list fields as field>
    ${field}<#if field_has_next>, </#if>
</#list>

}