<#if package?has_content>
package ${package}

</#if>
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase

<#if generatedAnnotation && generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = ["com.kobylynskyi.graphql.codegen.GraphQLCodegen"],
    date = "${generatedInfo.getDateTime()}"
)
</#if>
open class GraphqlJacksonTypeIdResolver : TypeIdResolverBase() {

    private var superType: JavaType? = null

    override fun init(baseType: JavaType?) {
        superType = baseType
    }

    override fun typeFromId(context: DatabindContext, typename: String): JavaType? {
        return try {
            val clazz = Class.forName(<#if package?has_content>"${package}." +
                    </#if><#if modelNamePrefix?has_content>"${modelNamePrefix}" +
                    </#if>typename<#if modelNamePrefix?has_content> +
                    "${modelNameSuffix}"</#if>)
            context.constructSpecializedType(superType, clazz)
        } catch (e: ClassNotFoundException) {
                System.err.println(e.message)
                null
        }
    }

    override fun getMechanism(): JsonTypeInfo.Id {
        return JsonTypeInfo.Id.NAME
    }

    override fun idFromValue(obj: Any): String {
        return idFromValueAndType(obj, obj.javaClass)
    }

    override fun idFromValueAndType(obj: Any, subType: Class<*>): String {
        return subType.simpleName
    }
}