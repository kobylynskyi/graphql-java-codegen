<#if package?has_content>
package ${package};

</#if>
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
public class ${className} extends GraphQLResponseProjection {

    public ${className}() {
    }

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
        fields.put("${field.name}", <#if field.type?has_content>subProjection<#else>null</#if>);
        return this;
    }

<#if field.parametrizedInputClassName?has_content>
    public ${className} ${field.name}(${field.parametrizedInputClassName} input<#if field.type?has_content>, ${field.type} subProjection</#if>) {
        parametrizedInputs.put("${field.name}", input);
        return ${field.name}(<#if field.type?has_content>subProjection</#if>);
    }

</#if>
</#list>
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
        return Objects.equals(fields, that.fields) && Objects.equals(parametrizedInputs, that.parametrizedInputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields, parametrizedInputs);
    }
</#if>

}
