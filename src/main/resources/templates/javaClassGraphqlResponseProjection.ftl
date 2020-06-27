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
<#if generatedInfo.getGeneratedType()?has_content>
@${generatedInfo.getGeneratedType()}(
    value = "com.kobylynskyi.graphql.codegen.GraphQLCodegen",
    date = "${generatedInfo.getDateTime()}"
)
</#if>
public class ${className} extends GraphQLResponseProjection {

    public ${className}() {
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
    public ${className} ${field.name}(<#if field.type?has_content>${field.type} subProjection</#if>) {
        return ${field.name}(<#if field.parametrizedInputClassName?has_content>(String)</#if>null<#if field.type?has_content>, subProjection</#if>);
    }

    public ${className} ${field.name}(String alias<#if field.type?has_content>, ${field.type} subProjection</#if>) {
        fields.add(new GraphQLResponseField("${field.name}").alias(alias)<#if field.type?has_content>.projection(subProjection)</#if>);
        return this;
    }

<#if field.parametrizedInputClassName?has_content>
    public ${className} ${field.name}(${field.parametrizedInputClassName} input<#if field.type?has_content>, ${field.type} subProjection</#if>) {
        return ${field.name}(null, input<#if field.type?has_content>, subProjection</#if>);
    }

    public ${className} ${field.name}(String alias, ${field.parametrizedInputClassName} input<#if field.type?has_content>, ${field.type} subProjection</#if>) {
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
