<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLTypeMapper"]>
<#if package?has_content>
package ${package}

</#if>
<#list imports as import>
import ${import}._
</#list>
<#assign waitImports = []>
<#list operations as operation>
    <#if operation.parameters?has_content>
        <#if enumImportItSelfInScala?has_content>
            <#list operation.parameters as param>
                <#list enumImportItSelfInScala as enum>
                    <#if MapperUtil.isScalaCollection(param.type)>
                        <#if enum == param.type?replace("Seq[", "")?replace("]", "")>
                            <#if waitImports?seq_contains(enum)>
                            <#else >
                                <#assign waitImports = waitImports + [param.type] />
                            </#if>
                        </#if>
                    <#else >
                        <#if enum == param.type>
                            <#if waitImports?seq_contains(enum)>
                            <#else >
                                <#assign waitImports = waitImports + [param.type] />
                            </#if>
                        </#if>
                    </#if>
                </#list>
            </#list>
        </#if>
    </#if>
</#list>
<#if waitImports?has_content>
<#list waitImports as import>
import ${import}._
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
    value = Array("com.kobylynskyi.graphql.codegen.GraphQLCodegen"),
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
<#if operation.deprecated?has_content>
    @${operation.deprecated.annotation}(message = "${operation.deprecated.reason}")
</#if>
<#list operation.annotations as annotation>
    @${annotation}
</#list>
    <#if operation.throwsException>
    @throws[Exception]
    </#if>
    def ${operation.name}(<#list operation.parameters as param>${param.name}: ${param.type}<#if param_has_next>, </#if></#list>): ${operation.type}

</#list>
}
