<#if package?has_content>
package ${package}

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection
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
<#if generatedAnnotation && generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = ["com.kobylynskyi.graphql.codegen.GraphQLCodegen"],
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
open class ${className} : GraphQLResponseProjection() {

<#if fields?has_content>
    override fun `all$`(): ${className} = `all$`(${responseProjectionMaxDepth})

    override fun `all$`(maxDepth: Int): ${className} {
    <#list fields as field>
        <#if field.type?has_content>
            <#if field.methodName?substring(0, 2) != "on">
        if (projectionDepthOnFields.getOrDefault("${className}.${field.type}.${field.methodName}", 0) <= maxDepth) {
            projectionDepthOnFields["${className}.${field.type}.${field.methodName}"] = projectionDepthOnFields.getOrDefault("${className}.${field.type}.${field.methodName}", 0) + 1
            this.${field.methodName}(${field.type}().`all$`(maxDepth - projectionDepthOnFields.getOrDefault("${className}.${field.type}.${field.methodName}", 0)))
        }
        </#if>
    <#else>
        this.${field.methodName}()
        </#if>
    </#list>
        return this
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
<#if field.deprecated?has_content>
    @${field.deprecated.annotation}(message = "${field.deprecated.reason}")
</#if>
    fun ${field.methodName}(<#if field.type?has_content>subProjection: ${field.type}</#if>): ${className} = ${field.methodName}(<#if field.parametrizedInputClassName?has_content></#if>null<#if field.type?has_content>, subProjection</#if>)

    fun ${field.methodName}(alias: String?<#if field.type?has_content>, subProjection: ${field.type}</#if>): ${className} {
        fields.add(GraphQLResponseField("${field.name}").alias(alias)<#if field.type?has_content>.projection(subProjection)</#if>)
        return this
    }

<#if field.parametrizedInputClassName?has_content>
    fun ${field.methodName}(input: ${field.parametrizedInputClassName}<#if field.type?has_content>, subProjection: ${field.type}</#if>): ${className} = ${field.methodName}(null, input<#if field.type?has_content>, subProjection</#if>)

    fun ${field.methodName}(alias: String?, input: ${field.parametrizedInputClassName}<#if field.type?has_content>, subProjection: ${field.type}</#if>): ${className} {
        fields.add(GraphQLResponseField("${field.name}").alias(alias).parameters(input)<#if field.type?has_content>.projection(subProjection)</#if>)
        return this
    }

</#if>
</#list>
</#if>
<#if equalsAndHashCode>
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val that = other as ${className}
        return Objects.equals(fields, that.fields)
    }

    override fun hashCode(): Int = Objects.hash(fields)
</#if>

}
