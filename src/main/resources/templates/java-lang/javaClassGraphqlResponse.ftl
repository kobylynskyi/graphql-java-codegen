<#if package?has_content>
package ${package};

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResult;
import java.util.Map;

<#if javaDoc?has_content>
/**
<#list javaDoc as javaDocLine>
 * ${javaDocLine}
</#list>
 */
</#if>
<#if generatedAnnotation && generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
public class ${className} extends GraphQLResult<Map<String, ${returnTypeName}>> {

    private static final String OPERATION_NAME = "${operationName}";

    public ${className}() {
    }

<#if javaDoc?has_content>
    /**
<#list javaDoc as javaDocLine>
     * ${javaDocLine}
</#list>
     */
</#if>
<#if deprecated?has_content>
    @${deprecated.annotation}
</#if>
    public ${returnTypeName} ${methodName}() {
        Map<String, ${returnTypeName}> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
