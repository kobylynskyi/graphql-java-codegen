<#if package?has_content>
package ${package};

</#if>
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

<#if generatedAnnotation && generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
public class GraphqlJacksonTypeIdResolver extends TypeIdResolverBase {

    private JavaType superType;

    @Override
    public void init(JavaType baseType) {
        superType = baseType;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String typename) {
        try {
            Class<?> clazz = Class.forName(
                <#if package?has_content>"${package}." +
                </#if><#if modelNamePrefix?has_content>"${modelNamePrefix}" +
                </#if>typename<#if modelNamePrefix?has_content> +
                "${modelNameSuffix}"</#if>
            );
            return context.constructSpecializedType(superType, clazz);
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.NAME;
    }

    @Override
    public String idFromValue(Object obj) {
        return idFromValueAndType(obj, obj.getClass());
    }

    @Override
    public String idFromValueAndType(Object obj, Class<?> subType) {
        return subType.getSimpleName();
    }
}