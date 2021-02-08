<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLTypeMapper"]>
<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult
import java.util.{ Map => JMap }

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
class ${className} extends GraphQLResult[JMap[String, ${returnTypeName}]] {

<#if javaDoc?has_content>
    /**
<#list javaDoc as javaDocLine>
     * ${javaDocLine}
</#list>
     */
</#if>
<#if deprecated?has_content>
    @${deprecated.annotation}(message = "${deprecated.reason}")
</#if>
    def ${methodName}(): ${returnTypeName} = {
        val data: JMap[String, ${returnTypeName}] = getData
    <#if MapperUtil.isScalaOption(returnTypeName)>
        if (data != null) data.get(${className}.OPERATION_NAME) else None
    <#else>
        <#if MapperUtil.isScalaPrimitive(returnTypeName)>
        data.get(${className}.OPERATION_NAME)
        <#else>
        if (data != null) data.get(${className}.OPERATION_NAME) else null
        </#if>
    </#if>
    }

}

object ${className} {

    private final val OPERATION_NAME: String = "${operationName}"

}
