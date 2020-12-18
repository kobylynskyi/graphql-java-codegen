<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLTypeMapper"]>
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
<#if fields?has_content>
    <#if enumImportItSelfInScala?has_content>
        <#list fields as field>
            <#list enumImportItSelfInScala as enum>
                <#if field.type?contains("Seq[")>
                    <#if enum == field.type?replace("Seq[", "")?replace("]", "")>
import ${enum}._
                    </#if>
                <#else >
                    <#if enum == field.type>
import ${enum}._
                    </#if>
                </#if>
            </#list>
        </#list>
    </#if>
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
<#if generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = Array("com.kobylynskyi.graphql.codegen.GraphQLCodegen"),
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
    <#if parentInterfaces?has_content><#list parentInterfaces as parent><#if parent == field.name>override </#if></#list></#if><#if !immutableModels>var <#else><#if parentInterfaces?has_content><#list parentInterfaces as parent><#if parent == field.name>val </#if></#list></#if></#if>${field.name}: ${field.type}<#if field.defaultValue?has_content> = <#if field.type?starts_with("Option[")><#if field.defaultValue!= "null">Some(${field.defaultValue})<#else>None</#if><#else>${field.defaultValue}</#if></#if><#if field_has_next>,</#if>
</#list>
</#if>
)<#if implements?has_content> extends <#list implements as interface>${interface}<#if interface_has_next> with </#if></#list></#if> {

<#if toString>
    override def toString(): String = {
    <#if fields?has_content>
        Seq(<#list fields as field><#assign realField = ""><#if field.type?starts_with("Option[") ><#assign realField = ".get"></#if>
            <#if MapperUtil.isScalaPrimitive(field.type)><#if toStringForRequest>"${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}<#if field.serializeUsingObjectMapper>, true</#if>)<#else>"${field.originalName}: " + ${field.name}</#if><#else><#if field.type?starts_with("Option[")>if (${field.name}.isDefined) <#else>if (${field.name} != null) </#if><#if toStringForRequest>"${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}${realField}<#if field.serializeUsingObjectMapper>, true</#if>)<#else><#if field.type == "String"> "${field.originalName}: \"${field.name}\"" <#else> "${field.originalName}: ${field.name}"</#if></#if> else ""</#if><#if field_has_next>,</#if></#list>
        ).filter(_ != "").mkString("{", ",", "}")
        <#else>
        "{}"
    </#if>
    }
</#if>
}

<#if builder>
object ${className} {

    def builder(): ${className}.Builder = new Builder()

    class Builder {

<#if fields?has_content>
    <#list fields as field>
        private var ${field.name}: ${field.type} = <#if field.defaultValue?has_content><#if field.type?starts_with("Option[")><#if field.defaultValue!= "null">Some(${field.defaultValue})<#else>None</#if><#else>${field.defaultValue}</#if><#else>_</#if>
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
        def set${field.name?replace("`", "")?cap_first}(${field.name}: ${field.type}): Builder = {
            this.${field.name} = ${field.name}
            this
        }

    </#list>
</#if>
        def build(): ${className} = ${className}(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>)

    }
}
</#if>