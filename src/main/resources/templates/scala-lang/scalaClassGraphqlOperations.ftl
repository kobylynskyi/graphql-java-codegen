<#if package?has_content>
package ${package}

</#if>
<#list imports as import>
import ${import}._
</#list>

<#if javaDoc?has_content>
/**
<#list javaDoc as javaDocLine>
 * ${javaDocLine}
</#list>
 */
</#if>
<#if generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
trait ${className}<#if implements?has_content> extends <#list implements as interface>${interface}<#if interface_has_next> with </#if></#list></#if> {

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
    <#if operation.throwsException>
    @throws[Exception]
    </#if>
    def ${operation.name}(<#list operation.parameters as param>${param.name}: ${param.type?replace('<','[')? replace('>',']')}<#if param_has_next>, </#if></#list>): ${operation.type?replace('<','[')? replace('>',']')}

</#list>
}