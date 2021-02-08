<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLTypeMapper"]>
<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
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
case class ${className}(
<#if fields?has_content>
<#list fields as field>
    <#if field.deprecated?has_content>
    @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
    </#if>
    <#list field.annotations as annotation>
    @${annotation}
    </#list>
    ${field.name}: ${field.type}<#if field.defaultValue?has_content> = <#if MapperUtil.isScalaOption(field.type)><#if field.defaultValue != "null">Some(${field.defaultValue})<#else>None</#if><#else>${field.defaultValue}</#if></#if><#if field_has_next>,</#if>
</#list>
</#if>
) extends GraphQLParametrizedInput {

    override def toString(): String = {<#--There is no Option[Seq[T]]-->
    <#if fields?has_content>
        Seq(<#list fields as field><#assign getMethod = ".get"><#assign asJava = ".asJava">
            <#if MapperUtil.isScalaPrimitive(field.type)>"${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name})<#if field_has_next>,</#if><#elseif MapperUtil.isScalaOption(field.type)>if (${field.name}.isDefined) "${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}${getMethod}) else ""<#if field_has_next>,</#if><#else>if (${field.name} != null)<#if MapperUtil.isScalaCollection(field.type)> "${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}${asJava}) else ""<#if field_has_next>,</#if><#else> "${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}) else ""<#if field_has_next>,</#if></#if></#if></#list>
        ).filter(_ != "").mkString("(", ",", ")")
    <#else>
        "()"
    </#if>
    }

}
