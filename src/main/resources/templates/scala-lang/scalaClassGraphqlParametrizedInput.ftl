<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLTypeMapper"]>
<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
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
    ${field.name}: ${field.type}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if><#if field_has_next>,</#if>
</#list>
</#if>
) extends GraphQLParametrizedInput {

    override def toString(): String = {
    <#if fields?has_content>
        Seq(
            <#list fields as field><#if MapperUtil.isScalaPrimitive(field.type)>"${field.originalName}: " + ${field.name}<#else>if (${field.name} != null) "${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}) else ""</#if><#if field_has_next>
            , </#if></#list>
        ).filter(_ != "").mkString("(", ",", ")")
    <#else>
        "()"
    </#if>
    }

}
