<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.java.JavaGraphQLTypeMapper"]>
<#if package?has_content>
package ${package};
</#if>

public record ${className} (
<#if fields?has_content>
    <#list fields as field>
    ${field.type} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if><#if field?has_next>,</#if>
    </#list>
</#if>
)
{

    public ${className}() {
	    <#list fields as field><#if field.defaultValue?has_content> ${field.name} = ${field.defaultValue};</#if></#list>
    }

}
