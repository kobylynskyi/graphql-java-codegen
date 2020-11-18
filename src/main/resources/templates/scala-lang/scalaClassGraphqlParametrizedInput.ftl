<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLParametrizedInput
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequestSerializer
import java.util.StringJoiner
<#if equalsAndHashCode>
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
class ${className} extends GraphQLParametrizedInput {

<#if fields?has_content>
<#list fields as field>
<#if field.deprecated>
    @Deprecated
</#if>
<#list field.annotations as annotation>
    @${annotation}
</#list>
    private var ${field.name}: ${field.type?replace('<','[')? replace('>',']')} = <#if field.defaultValue?has_content>${field.defaultValue}<#else>_</#if>
</#list>
</#if>

<#if fields?has_content>
    def this(<#list fields as field>${field.name}: ${field.type?replace('<','[')? replace('>',']')}<#if field_has_next>, </#if></#list>) {
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
    def ${field.name}(${field.name}: ${field.type?replace('<','[')? replace('>',']')}): ${className} = {
        this.${field.name} = ${field.name}
        this
    }

</#list>
</#if>
<#if equalsAndHashCode>
    override def equals(obj: AnyRef): Boolean = {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        val ${className} that = obj.asInstanceOf[${className}]
<#if fields?has_content>
        return <#list fields as field>Objects.equals(${field.name}, that.${field.name})<#if field_has_next>
            && </#if></#list>
<#else>
        true
</#if>
    }

    override def hashCode(): Int = {
<#if fields?has_content>
        return Objects.hash(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>)
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
            joiner.add(s"${field.originalName}: $GraphQLRequestSerializer.getEntry(${field.name})")
        }
</#list>
</#if>
        joiner.toString()
    }

}
