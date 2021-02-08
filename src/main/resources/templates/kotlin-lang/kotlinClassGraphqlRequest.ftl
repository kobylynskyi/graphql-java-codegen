<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.kotlin.KotlinGraphQLTypeMapper"]>
<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest
<#if toString || equalsAndHashCode>
import java.util.Objects
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
    value = ["com.kobylynskyi.graphql.codegen.GraphQLCodegen"],
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
open class ${className}(private val alias: String?) : GraphQLOperationRequest {

    companion object {
        const val OPERATION_NAME: String = "${operationName}"
        val OPERATION_TYPE: GraphQLOperation = GraphQLOperation.${operationType}
<#if builder>

        fun builder(): Builder = Builder()
</#if>
    }

    private val input: MutableMap<String, Any?> = LinkedHashMap()
    private val useObjectMapperForInputSerialization: MutableSet<String> = HashSet()

    constructor(): this(null)

<#if fields?has_content>
<#list fields as field>
<#if field.javaDoc?has_content>
    /**
<#list field.javaDoc as javaDocLine>
     * ${javaDocLine}
</#list>
     */
</#if>
<#if field.deprecated?has_content>
    @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
</#if>
    fun set${field.name?replace("`", "")?cap_first}(${field.name}: ${field.type}) {
        this.input["${field.originalName}"] = ${field.name}
        <#if field.serializeUsingObjectMapper>
        this.useObjectMapperForInputSerialization.add("${field.originalName}");
        </#if>
    }

</#list>
</#if>
    override fun getOperationType(): GraphQLOperation = OPERATION_TYPE

    override fun getOperationName(): String = OPERATION_NAME

    override fun getAlias(): String? = alias ?: OPERATION_NAME

    override fun getInput(): MutableMap<String, Any?> = input

    override fun getUseObjectMapperForInputSerialization(): MutableSet<String> = useObjectMapperForInputSerialization
<#if equalsAndHashCode>

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ${className}
        return Objects.equals(operationType, that.operationType) &&
                   Objects.equals(operationName, that.operationName) &&
                   Objects.equals(input, that.input)
    }

    override fun hashCode(): Int = Objects.hash(operationType, operationName, input)
</#if>
<#if toString>

    override fun toString(): String = Objects.toString(input)
</#if>

<#if builder>
    class Builder {

        private var `$alias`: String? = null
    <#if fields?has_content>
        <#list fields as field>
        <#if field.defaultValue?has_content>
        private var ${field.name}: ${field.type} = ${field.defaultValue}
        <#else>
        <#if field.type?ends_with("?")>
        private var ${field.name}: ${field.type} = null
        <#else>
        <#if MapperUtil.isKotlinPrimitive(field.type)>
        <#assign default = MapperUtil.defaultValueKotlinPrimitive(field.type)/>
        private var ${field.name}: ${field.type} = default
        <#else>
        private lateinit var ${field.name}: ${field.type}
        </#if>
        </#if>
        </#if>
        </#list>
    </#if>

        fun alias(alias: String?): Builder {
            this.`$alias` = alias
            return this
        }

    <#if fields?has_content>
        <#list fields as field>
            <#if field.javaDoc?has_content>
        /**
        <#list field.javaDoc as javaDocLine>
         * ${javaDocLine}
        </#list>
         */
    </#if>
    <#if field.deprecated?has_content>
        @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
    </#if>
        fun set${field.name?replace("`", "")?cap_first}(${field.name}: ${field.type}): Builder {
            this.${field.name} = ${field.name}
            return this
        }
        </#list>
    </#if>

        fun build(): ${className} {
            val obj = ${className}(`$alias`)
        <#if fields?has_content>
            <#list fields as field>
            obj.set${field.name?replace("`", "")?cap_first}(${field.name})
            </#list>
        </#if>
            return obj
        }

    }
</#if>
}
