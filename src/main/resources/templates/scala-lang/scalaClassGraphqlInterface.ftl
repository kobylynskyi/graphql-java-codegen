<#if package?has_content>
package ${package}

</#if>
<#if imports??>
<#list imports as import>
import ${import}._
</#list>
</#if>
<#if fields?has_content>
    <#if enumImportItSelfInScala?has_content>
        <#list fields as field>
            <#list enumImportItSelfInScala as enum>
                <#if field.type?contains("Seq[")>
                    <#if enum == field.type?replace("Seq[", "")?replace("]", "")>
import ${field.type?replace("Seq[", "")?replace("]", "")}._
                    </#if>
                <#else >
                    <#if enum == field.type>
import ${field.type}._
                    </#if>
                </#if>
            </#list>
        </#list>
    </#if>
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
trait ${className}<#if implements?has_content> extends<#list implements as interface> ${interface}<#if interface_has_next> with </#if></#list></#if> {

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
<#list field.annotations as annotation>
    @${annotation}
</#list>
    def get${field.name?cap_first}(): ${field.type}

</#list>
</#if>
}