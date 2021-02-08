<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult

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
open class ${className} : GraphQLResult<MutableMap<String, ${returnTypeName}>>() {

    companion object {
        const val OPERATION_NAME: String = "${operationName}"
    }

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
    fun ${methodName}(): ${returnTypeName} {
        val data: MutableMap<String, ${returnTypeName}> = super.getData()
        <#if returnTypeName?ends_with("?")>
        return data[OPERATION_NAME]
        <#else>
        return data.getValue(OPERATION_NAME)
        </#if>
    }
}
