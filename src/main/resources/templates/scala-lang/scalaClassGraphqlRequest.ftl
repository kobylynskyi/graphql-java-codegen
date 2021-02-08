<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.scala.ScalaGraphQLTypeMapper"]>
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
import scala.collection.mutable
import scala.collection.JavaConverters._
<#if fields?has_content>
    <#if enumImportItSelfInScala?has_content>
        <#list fields as field>
            <#list enumImportItSelfInScala as enum>
                <#if MapperUtil.isScalaCollection(field.type)>
                    <#if enum == field.type?replace("Seq[", "")?replace("]", "")>
import ${enum}._
                    </#if>
                <#else >
                    <#if enum == field.type>
import ${enum}._
                    </#if>
                </#if>
            </#list>
        </#list>
    </#if>
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
    value = Array("com.kobylynskyi.graphql.codegen.GraphQLCodegen"),
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
class ${className}(alias: String) extends GraphQLOperationRequest {

    <#--use Any be prepared for any contingency-->
    private final lazy val input = new JLinkedHashMap[String, java.lang.Object]()
    private final lazy val useObjectMapperForInputSerialization: mutable.Set[String] = mutable.Set()

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
    def set${field.name?replace("`", "")?cap_first}(${field.name}: ${field.type}): Unit = {
        <#if MapperUtil.isScalaPrimitive(field.type)>
        this.input.put("${field.originalName}", ${field.type}.box(${field.name}))
        <#else>
        this.input.put("${field.originalName}", ${field.name})
        </#if>
        <#if field.serializeUsingObjectMapper>
        this.useObjectMapperForInputSerialization.add("${field.originalName}");
        </#if>
    }

</#list>
</#if>
    override def getOperationType(): GraphQLOperation = ${className}.OPERATION_TYPE

    override def getOperationName(): String = ${className}.OPERATION_NAME

    override def getAlias(): String = if (alias != null) alias else ${className}.OPERATION_NAME

    override def getInput(): JMap[String, java.lang.Object] = input

    override def getUseObjectMapperForInputSerialization(): java.util.Set[String] = useObjectMapperForInputSerialization.asJava
<#if equalsAndHashCode>

    override def equals(obj: Any): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass != obj.getClass) {
            return false
        }
        val that = obj.asInstanceOf[${className}]
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

object ${className} {

    final val OPERATION_NAME: String = "${operationName}"
    final val OPERATION_TYPE: GraphQLOperation = GraphQLOperation.${operationType}

    <#--  use apply create instance  -->
    def apply(alias: String) = new ${className}(alias)

    def apply() = new ${className}(null)

    <#if builder>

    def builder(): Builder = new Builder()

    class Builder {

        private var $alias: String = _
        <#if fields?has_content>
            <#list fields as field>
        private var ${field.name}: ${field.type} = <#if field.defaultValue?has_content><#if MapperUtil.isScalaOption(field.type)><#if field.defaultValue!= "null">Some(${field.defaultValue})<#else>None</#if><#else>${field.defaultValue}</#if><#else>_</#if>
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
        <#if field.deprecated?has_content>
        @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
        </#if>
        def set${field.name?replace("`", "")?cap_first}(${field.name}: ${field.type}): Builder = {
            this.${field.name} = ${field.name}
            this
        }
    </#list>
        </#if>

        def build(): ${className} = {
            val obj = new ${className}($alias)
        <#if fields?has_content>
            <#list fields as field>
            obj.set${field.name?replace("`", "")?cap_first}(${field.name})
            </#list>
        </#if>
            obj
        }

    }
    </#if>
}
