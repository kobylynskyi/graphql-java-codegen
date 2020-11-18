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
<#if generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
class ${className} extends GraphQLResult[Map[String, ${returnTypeName}]] {

<#if javaDoc?has_content>
    /**
<#list javaDoc as javaDocLine>
     * ${javaDocLine}
</#list>
     */
</#if>
<#if deprecated>
    @Deprecated
</#if>
    def ${methodName}(): ${returnTypeName?replace('<','[')? replace('>',']')} = {
        val data: JMap[String, ${returnTypeName}] = getData()
        if (data != null) data.get(${className}.OPERATION_NAME) else null
    }

}

object ${className} {

    private final val OPERATION_NAME: String = "${operationName}"

}
