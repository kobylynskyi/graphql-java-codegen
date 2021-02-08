<#if package?has_content>
package ${package};

</#if>
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseField;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLResponseProjection;
<#if equalsAndHashCode>
import java.util.Objects;
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
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
<#list annotations as annotation>
@${annotation}
</#list>
public class ${className} extends GraphQLResponseProjection {

    public ${className}() {
    }
<#if fields?has_content>

    @Override
    public ${className} all$() {
        return all$(${responseProjectionMaxDepth});
    }

    @Override
    public ${className} all$(int maxDepth) {
    <#list fields as field>
        <#if field.type?has_content>
            <#if field.methodName?substring(0,2) != "on">
        if (projectionDepthOnFields.getOrDefault("${className}.${field.type}.${field.methodName}", 0) <= maxDepth) {
            projectionDepthOnFields.put("${className}.${field.type}.${field.methodName}", projectionDepthOnFields.getOrDefault("${className}.${field.type}.${field.methodName}", 0) + 1);
            this.${field.methodName}(new ${field.type}().all$(maxDepth - projectionDepthOnFields.getOrDefault("${className}.${field.type}.${field.methodName}", 0)));
        }
        </#if>
    <#else>
        this.${field.methodName}();
        </#if>
    </#list>
        return this;
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
    @${field.deprecated.annotation}
</#if>
    public ${className} ${field.methodName}(<#if field.type?has_content>${field.type} subProjection</#if>) {
        return ${field.methodName}(<#if field.parametrizedInputClassName?has_content>(String)</#if>null<#if field.type?has_content>, subProjection</#if>);
    }

    public ${className} ${field.methodName}(String alias<#if field.type?has_content>, ${field.type} subProjection</#if>) {
        fields.add(new GraphQLResponseField("${field.name}").alias(alias)<#if field.type?has_content>.projection(subProjection)</#if>);
        return this;
    }

<#if field.parametrizedInputClassName?has_content>
    public ${className} ${field.methodName}(${field.parametrizedInputClassName} input<#if field.type?has_content>, ${field.type} subProjection</#if>) {
        return ${field.methodName}(null, input<#if field.type?has_content>, subProjection</#if>);
    }

    public ${className} ${field.methodName}(String alias, ${field.parametrizedInputClassName} input<#if field.type?has_content>, ${field.type} subProjection</#if>) {
        fields.add(new GraphQLResponseField("${field.name}").alias(alias).parameters(input)<#if field.type?has_content>.projection(subProjection)</#if>);
        return this;
    }

</#if>
</#list>
</#if>
<#if equalsAndHashCode>
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ${className} that = (${className}) obj;
        return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }
</#if>

}
