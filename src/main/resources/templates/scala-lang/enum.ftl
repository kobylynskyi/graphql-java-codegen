<#if package?has_content>
package ${package}

</#if>
<#if serializationLibrary == 'JACKSON'>
import com.fasterxml.jackson.core.`type`.TypeReference

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
<#list annotations as annotation>
@${annotation}
</#list>
object ${className} extends Enumeration<#if implements?has_content> with<#list implements as interface> ${interface}<#if interface_has_next> with </#if></#list></#if> {

    type ${className} = Value

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
    val ${field.javaName}: Value = Value("${field.graphqlName}")
</#list>
</#if>

}
<#if serializationLibrary == 'JACKSON'>

class ${className}TypeRefer extends TypeReference[${className}.type]
</#if>
