<#if package?has_content>
package ${package}

</#if>
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase

<#if generatedAnnotation && generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = Array("com.kobylynskyi.graphql.codegen.GraphQLCodegen"),
    date = "${generatedInfo.getDateTime()}"
)
</#if>
class GraphqlJacksonTypeIdResolver extends TypeIdResolverBase {

    private var superType: JavaType = _

    override def init(baseType: JavaType): Unit = {
        superType = baseType
    }

    override def typeFromId(context: DatabindContext, typename: String): JavaType = try {
        val clazz = Class.forName(<#if package?has_content>"${package}." +
                    </#if><#if modelNamePrefix?has_content>"${modelNamePrefix}" +
                    </#if>typename<#if modelNameSuffix?has_content> +
                    "${modelNameSuffix}"</#if>)
        context.constructSpecializedType(superType, clazz)
    } catch {
        case e: ClassNotFoundException =>
        Console.err.println(e.getMessage)
        null
    }

    override def getMechanism = JsonTypeInfo.Id.NAME

    override def idFromValue(obj: Any): String = idFromValueAndType(obj, obj.getClass)

    override def idFromValueAndType(obj: Any, subType: Class[_]): String = subType.getSimpleName
}