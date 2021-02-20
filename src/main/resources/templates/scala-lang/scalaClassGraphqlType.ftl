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
<#if generateModelOpenClasses && equalsAndHashCode>
import java.util.Objects
</#if>
import scala.collection.JavaConverters._
<#if fields?has_content>
    <#if enumImportItSelfInScala?has_content>
        <#list fields as field>
            <#list enumImportItSelfInScala as enum>
                <#if MapperUtil.isScalaCollection(field.type)>
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
<#if generatedAnnotation && generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = Array("com.kobylynskyi.graphql.codegen.GraphQLCodegen"),
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
<#if !generateModelOpenClasses>case class<#else>class</#if> ${className}(
<#if fields?has_content>
<#list fields as field>
  <#if field.deprecated?has_content>
    @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
  </#if>
<#list field.annotations as annotation>
    @${annotation}
</#list>
    <#if parentInterfaces?has_content><#list parentInterfaces as parent><#if parent == field.name>override </#if></#list></#if><#if !immutableModels>var <#else><#if parentInterfaces?has_content><#list parentInterfaces as parent><#if parent == field.name>val </#if></#list></#if></#if>${field.name}: ${field.type}<#if field.defaultValue?has_content> = <#if MapperUtil.isScalaOption(field.type)><#if field.defaultValue != "null">Some(${field.defaultValue})<#else>None</#if><#else>${field.defaultValue}</#if></#if><#if field_has_next>,</#if>
</#list>
</#if>
)<#if implements?has_content> extends <#list implements as interface>${interface}<#if interface_has_next> with </#if></#list></#if> {

<#if toString>
    override def toString(): String = {
    <#if fields?has_content><#-- When you modify it, copy it out and make sure it is one line after modification, There is no Option[Seq[T]]. -->
        Seq(<#list fields as field><#assign getMethod = ""><#assign asJava = ""><#if MapperUtil.isScalaOption(field.type)><#assign getMethod = ".get"></#if><#if MapperUtil.isScalaCollection(field.type)><#assign asJava = ".asJava"></#if>
            <#if MapperUtil.isScalaPrimitive(field.type)><#if toStringForRequest>"${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}<#if field.serializeUsingObjectMapper>, true</#if>)<#else>"${field.originalName}: " + ${field.name}</#if><#else><#if MapperUtil.isScalaOption(field.type)>if (${field.name}.isDefined) <#else>if (${field.name} != null) </#if><#if toStringForRequest>"${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}${getMethod}${asJava}<#if field.serializeUsingObjectMapper>, true</#if>)<#else><#if field.type == "String"> "${field.originalName}: \"${field.name}\"" <#else> "${field.originalName}: ${field.name}"</#if></#if> else ""</#if><#if field_has_next>,</#if></#list>
        ).filter(_ != "").mkString("{", ",", "}")
        <#else><#--Keep it on one line to make sure the code style remains the same-->
        "{}"
    </#if>
    }
</#if>
<#if generateModelOpenClasses && equalsAndHashCode>

    override def equals(obj: Any): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass != obj.getClass) {
            return false
        }
        val that = obj.asInstanceOf[${className}]
    <#if fields?has_content>
        <#list fields as field>
        Objects.equals(${field.name}, that.${field.name})<#if field_has_next> &&
        </#if></#list>
    <#else>
        true</#if>
    }

    override def hashCode(): Int = {
    <#if fields?has_content>
        Objects.hash(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>)
    <#else>
        0
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
        private var ${field.name}: ${field.type} = <#if field.defaultValue?has_content><#if MapperUtil.isScalaOption(field.type)><#if field.defaultValue!= "null">Some(${field.defaultValue})<#else>None</#if><#else>${field.defaultValue}</#if><#else>_</#if>
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
        <#if field.deprecated?has_content>
        @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
        </#if>
        def set${field.name?replace("`", "")?cap_first}(${field.name}: ${field.type}): Builder = {
            this.${field.name} = ${field.name}
            this
        }

    </#list>
</#if>
        def build(): ${className} = <#if generateModelOpenClasses>new ${className}<#else>${className}</#if>(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>)

    }
}
</#if>
