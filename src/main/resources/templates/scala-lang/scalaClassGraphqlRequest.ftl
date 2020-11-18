<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperationRequest
import java.util.{ LinkedHashMap => JLinkedHashMap }
import java.util.{ Map => JMap }
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
<#if generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
class ${className} extends GraphQLOperationRequest {

    private var alias: String = _
    private final lazy val input = new JLinkedHashMap[String, AnyRef]()

    def this(alias: String) {
        this()
        this.alias = alias
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
<#if field.deprecated>
    @Deprecated
</#if>
    def set${field.name?cap_first}(${field.name}: ${field.type?replace('<','[')? replace('>',']')} ): Unit = {
        this.input.put("${field.originalName}", ${field.name})
    }

</#list>
</#if>
    override def getOperationType(): GraphQLOperation = ${className}.OPERATION_TYPE

    override def getOperationName(): String = ${className}.OPERATION_NAME

    override def getAlias(): String = if (alias != null) alias else ${className}.OPERATION_NAME

    override def getInput(): JMap[String, AnyRef] = input

<#if equalsAndHashCode>
    override def equals(Object obj): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        val ${className} that = obj.asInstanceOf[${className}]
        Objects.equals(getOperationType(), that.getOperationType()) &&
                   Objects.equals(getOperationName(), that.getOperationName()) &&
                   Objects.equals(input, that.input)
    }

    override def hashCode(): Int = Objects.hash(getOperationType(), getOperationName(), input)
</#if>
<#if toString>
    override def toString(): String = Objects.toString(input)
</#if>
}

<#if builder>
object ${className} {

    final val OPERATION_NAME: String = "${operationName}"
    final val OPERATION_TYPE: GraphQLOperation = GraphQLOperation.${operationType}

    def builder(): Builder = new Builder()

    class Builder {

        private var $alias: String = _
        <#if fields?has_content>
            <#list fields as field>
        private var ${field.name}: ${field.type?replace('<','[')? replace('>',']')} = <#if field.defaultValue?has_content> ${field.defaultValue} <#else>_</#if>
            </#list>
        </#if>

        def alias(alias: String): Builder = {
            this.$alias = alias
            this
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
        <#if field.deprecated>
        @Deprecated
        </#if>
        def set${field.name?cap_first}(${field.name}: ${field.type} ): Builder = {
            this.${field.name} = ${field.name}
            this
        }
    </#list>
        </#if>

        def build(): ${className} = {
            val obj = new ${className}($alias)
        <#if fields?has_content>
            <#list fields as field>
            obj.set${field.name?cap_first}(${field.name})
            </#list>
        </#if>
            obj
        }

    }
}
</#if>