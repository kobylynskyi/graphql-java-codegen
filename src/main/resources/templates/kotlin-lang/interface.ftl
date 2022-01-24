<#if package?has_content>
package ${package}

</#if>
<#if imports??>
    <#list imports as import>
import ${import}.*
    </#list>
</#if>
<#assign duplicateParentInterfaces = [] />
<#assign parentInterfaces = [] />
<#if fields?has_content>
    <#if parentInterfaceProperties?has_content>
        <#list implements as implement>
            <#list parentInterfaceProperties?keys as parentInterface>
                <#if implement == parentInterface>
                    <#assign duplicateParentInterfaces = duplicateParentInterfaces + parentInterfaceProperties["${parentInterface}"] />
                <#else >
                </#if>
            </#list>
        </#list>
    </#if>
</#if>
<#--duplicate removal-->
<#if duplicateParentInterfaces?has_content>
    <#list duplicateParentInterfaces as duplicateParentInterface>
        <#if !parentInterfaces?seq_contains(duplicateParentInterface)>
            <#assign parentInterfaces = parentInterfaces + [duplicateParentInterface]>
        </#if>
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
    value = ["com.kobylynskyi.graphql.codegen.GraphQLCodegen"],
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
<#if generateSealedInterfaces>sealed </#if>interface ${className}<#if implements?has_content> : <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if> {

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
    @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
    </#if>
    <#list field.annotations as annotation>
    @get:${annotation}
    </#list>
    <#if parentInterfaces?has_content><#list parentInterfaces as parent><#if parent == field.name>override </#if></#list></#if><#if !immutableModels>var <#else>val </#if>${field.name}: ${field.type}

    </#list>
</#if>
}
