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
    value = Array("com.kobylynskyi.graphql.codegen.GraphQLCodegen"),
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
class ${className} extends GraphQLResponseProjection {

<#if fields?has_content>
    override def all$(): ${className} = all$(${responseProjectionMaxDepth})

    override def all$(maxDepth: Int): ${className} = {
    <#list fields as field>
        <#if field.type?has_content>
            <#if field.methodName?substring(0, 2) != "on">
        if (projectionDepthOnFields.getOrDefault("${className}.${field.type}.${field.methodName}", 0) <= maxDepth) {
            projectionDepthOnFields.put("${className}.${field.type}.${field.methodName}", projectionDepthOnFields.getOrDefault("${className}.${field.type}.${field.methodName}", 0) + 1)
            this.${field.methodName}(new ${field.type}().all$(maxDepth - projectionDepthOnFields.getOrDefault("${className}.${field.type}.${field.methodName}", 0)))
        }
        </#if>
    <#else>
        this.${field.methodName}()
        </#if>
    </#list>
        this
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
    def ${field.methodName}(<#if field.type?has_content>subProjection: ${field.type}</#if>): ${className} = {
        ${field.methodName}(<#if field.parametrizedInputClassName?has_content></#if>null.asInstanceOf[String]<#if field.type?has_content>, subProjection</#if>)
    }

    def ${field.methodName}(alias: String<#if field.type?has_content>, subProjection: ${field.type}</#if>): ${className} = {
        fields.add(new GraphQLResponseField("${field.name}").alias(alias)<#if field.type?has_content>.projection(subProjection)</#if>)
        this
    }

<#if field.parametrizedInputClassName?has_content>
    def ${field.methodName}(input: ${field.parametrizedInputClassName}<#if field.type?has_content>,subProjection: ${field.type}</#if>): ${className} = {
        ${field.methodName}(null.asInstanceOf[String], input<#if field.type?has_content>, subProjection</#if>)
    }

    def ${field.methodName}(alias: String, input: ${field.parametrizedInputClassName} <#if field.type?has_content>, subProjection: ${field.type}</#if>): ${className} = {
        fields.add(new GraphQLResponseField("${field.name}").alias(alias).parameters(input)<#if field.type?has_content>.projection(subProjection)</#if>)
        this
    }

</#if>
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
        Objects.equals(fields, that.fields)
    }

    override def hashCode(): Int = Objects.hash(fields)
</#if>

}
