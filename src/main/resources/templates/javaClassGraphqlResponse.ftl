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
<#if deprecated>
    @Deprecated
</#if>
    public ${returnTypeName} ${operationName}() {
        Map<String, ${returnTypeName}> data = getData();
        return data != null ? data.get(OPERATION_NAME) : null;
    }

}
