<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.mapper.GraphqlTypeToJavaTypeMapper"]>
<#if package?has_content>
package ${package}

</#if>
<#if imports??>
<#list imports as import>
import ${import}.*
</#list>
</#if>
<#if toStringForRequest>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
</#if>
<#if equalsAndHashCode>
import java.util.Objects
</#if>
<#if toString>
import java.util.StringJoiner
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
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
case class ${className}(
<#if fields?has_content>
<#list fields as field>
    <#if field.deprecated>
        @Deprecated
    </#if>
<#list field.annotations as annotation>
    @${annotation}
</#list>
    <#if !immutableModels>var <#else>val </#if>${field.name}: ${field.type?replace('<','[')? replace('>',']')}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if><#if field_has_next>,</#if>
    </#list>
</#if>
)<#if implements?has_content> extends <#list implements as interface>${interface}<#if interface_has_next> with </#if></#list></#if> {

<#if toString>
    override def toString(): String = {
        val joiner = new StringJoiner(", ", "{ ", " }")
<#if fields?has_content>
<#list fields as field>
  <#if MapperUtil.isJavaPrimitive(field.type)>
    <#if toStringForRequest>
        joiner.add(s"${field.originalName}: $GraphQLRequestSerializer.getEntry(${field.name})")
    <#else>
        joiner.add(s"${field.originalName}: $${field.name}")
    </#if>
  <#else>
        if (${field.name} != null) {
    <#if toStringForRequest>
            joiner.add(s"${field.originalName}: $GraphQLRequestSerializer.getEntry(${field.name})")
    <#else>
      <#if field.type == "String">
            joiner.add(s"${field.originalName}: \"$${field.name}\"")
      <#else>
            joiner.add(s"${field.originalName}: $${field.name}")
      </#if>
    </#if>
        }
  </#if>
</#list>
</#if>
        joiner.toString()
    }
</#if>
}

<#if builder>
object ${className} {

    def builder(): ${className}.Builder = new Builder()

    class Builder {

<#if fields?has_content>
    <#list fields as field>
        private var ${field.name}: ${field.type?replace('<','[')? replace('>',']')} = <#if field.defaultValue?has_content>${field.defaultValue}<#else>_</#if>
    </#list>
</#if>

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
        def set${field.name?cap_first}(${field.name}: ${field.type?replace('<','[')? replace('>',']')}): Builder = {
            this.${field.name} = ${field.name}
            this
        }

    </#list>
</#if>
        def build(): ${className} = ${className}(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>)

    }
}
</#if>