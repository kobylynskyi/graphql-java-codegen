<#assign MapperUtil=statics["com.kobylynskyi.graphql.codegen.kotlin.KotlinGraphQLTypeMapper"]>
<#if package?has_content>
package ${package}

</#if>
<#if imports??>
    <#list imports as import>
import ${import}.*
    </#list>
</#if>
<#if toStringForRequest>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
</#if>
<#if generateModelOpenClasses && equalsAndHashCode>
import java.util.Objects
</#if>
<#if toString>
import java.util.StringJoiner
</#if>
<#assign duplicateParentInterfaces = [] />
<#assign parentInterfaces = [] />
<#if fields?has_content>
    <#if parentInterfaceProperties?has_content>
        <#list implements as implement>
            <#list parentInterfaceProperties?keys as parentInterface>
                <#if implement == parentInterface>
                    <#assign duplicateParentInterfaces = duplicateParentInterfaces + parentInterfaceProperties["${parentInterface}"] />
                <#else >
                </#if>
            </#list>
        </#list>
    </#if>
</#if>
<#--duplicate removal-->
<#if duplicateParentInterfaces?has_content>
    <#list duplicateParentInterfaces as duplicateParentInterface>
    <#if !parentInterfaces?seq_contains(duplicateParentInterface)>
    <#assign parentInterfaces = parentInterfaces + [duplicateParentInterface]>
    </#if>
    </#list>
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
<#if !fields?has_content>
open class ${className}()<#if implements?has_content> : <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if>
<#else>
<#if generateModelOpenClasses>open class<#else>data class</#if> ${className}(
<#if fields?has_content>
<#list fields as field>
    <#if field.deprecated?has_content>
    @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
    </#if><#-- Properties of multiple interfaces should not have duplicate names  -->
    <#if parentInterfaces?has_content><#list parentInterfaces as parent><#if parent == field.name>override
    </#if></#list></#if><#if !immutableModels><#list field.annotations as annotation>@get:${annotation}
    </#list>var <#else><#list field.annotations as annotation>@get:${annotation}
    </#list>val </#if>${field.name}: ${field.type}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if><#if field_has_next>,</#if>
</#list>
</#if>
)<#if implements?has_content> : <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if> {

<#if builder>
    companion object {
        fun builder(): Builder = Builder()
    }

</#if>
<#if toString>
    // In the future, it maybe change.
    override fun toString(): String {
        val joiner = StringJoiner(", ", "{ ", " }")
    <#if fields?has_content>
        <#list fields as field>
    <#if field.type?ends_with("?")>
        if (${field.name} != null) {
        <#if toStringForRequest>
            joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}))
        <#else>
        <#if field.type == "String?">
            joiner.add("${field.originalName}: \"" + ${field.name} + "\"");
        <#else>
            joiner.add("${field.originalName}: " + ${field.name});
        </#if>
        </#if>
        }
    <#else>
        <#if toStringForRequest>
        joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}))
        <#else>
    <#if field.type == "String">
        joiner.add("${field.originalName}: \"" + ${field.name} + "\"");
    <#else>
        joiner.add("${field.originalName}: " + ${field.name});
    </#if>
        </#if>
    </#if>
        </#list>
    </#if>
        return joiner.toString()
    }
</#if>
<#if generateModelOpenClasses && equalsAndHashCode>

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ${className}
        <#if fields?has_content>
        return <#list fields as field>Objects.equals(${field.name}, that.${field.name})<#if field_has_next>
                && </#if></#list>
        <#else>
        return true</#if>
    }

    override fun hashCode(): Int = {
    <#if fields?has_content>
        return Objects.hash(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>)
    <#else>
        return 0
    </#if>
    }
</#if>
<#if builder>

    class Builder {

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
        private var ${field.name}: ${field.type} = ${default}
    <#else>
        private lateinit var ${field.name}: ${field.type}
    </#if>
    </#if>
    </#if>
        </#list>
    </#if>

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
            return ${className}(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>)
        }
    }
</#if>
}
</#if>
