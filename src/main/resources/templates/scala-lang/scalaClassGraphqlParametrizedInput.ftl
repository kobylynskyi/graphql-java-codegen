<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import java.util.StringJoiner
<#if equalsAndHashCode>
import java.util.Objects
</#if>
<#if fields?has_content>
    <#if enumImportItSelfInScala?has_content>
        <#list fields as field>
            <#list enumImportItSelfInScala as enum>
                <#if field.type?contains("Seq[")>
                    <#if enum == field.type?replace("Seq[", "")?replace("]", "")>
import ${field.type?replace("Seq[", "")?replace("]", "")}._
                    </#if>
                <#else >
                    <#if enum == field.type>
import ${field.type}._
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
<#if generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = Array("com.kobylynskyi.graphql.codegen.GraphQLCodegen"),
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
class ${className} extends GraphQLParametrizedInput {

<#if fields?has_content>
<#list fields as field>
<#if field.deprecated>
    @Deprecated
</#if>
<#list field.annotations as annotation>
    @${annotation}
</#list>
    private var ${field.name}: ${field.type} = <#if field.defaultValue?has_content>${field.defaultValue}<#else>_</#if>
</#list>
</#if>

<#if fields?has_content>
    def this(<#list fields as field>${field.name}: ${field.type}<#if field_has_next>, </#if></#list>) {
        this()
    <#list fields as field>
        this.${field.name} = ${field.name}
</#list>
    }
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
<#if field.deprecated>
    @Deprecated
</#if>
    def ${field.name}(${field.name}: ${field.type}): ${className} = {
        this.${field.name} = ${field.name}
        this
    }

</#list>
</#if>
<#if equalsAndHashCode>
    override def equals(obj: Any): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass != obj.getClass) {
            return false
        }
        val that = obj.asInstanceOf[${className}]
        <#if fields?has_content>
        Seq(
            <#list fields as field>Objects.equals(${field.name}, that.${field.name})<#if field_has_next>
            , </#if></#list>
        ).forall(o => o)
        <#else>
        true
        </#if>

    }

    override def hashCode(): Int = {
<#if fields?has_content>
        Objects.hash(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>)
<#else>
        0
</#if>
    }
</#if>

    override def toString(): String = {
        val joiner = new StringJoiner(", ", "(", ")")
<#if fields?has_content>
<#list fields as field>
        if (${field.name} != null) {
            joiner.add("${field.originalName}: " + GraphQLRequestSerializer.getEntry(${field.name}))
        }
</#list>
</#if>
        joiner.toString
    }

}
