<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>

public class ${className} implements GraphQLResponseProjection {

    private Map<String, Object> fields = new LinkedHashMap<>();

    public ${className}() {
    }

<#list fields as field>
    public ${className} ${field.name}(<#if field.type?has_content>${field.type} subProjection</#if>) {
        fields.put("${field.name}", <#if field.type?has_content>subProjection<#else>null</#if>);
        return this;
    }

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
        return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }
</#if>

    @Override
    public String toString() {
        if (fields.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(" ", "{ ", " }");
        for (Map.Entry<String, Object> property : fields.entrySet()) {
            joiner.add(property.getKey());
            if (property.getValue() != null) {
                joiner.add(" ").add(property.getValue().toString());
            }
        }
        return joiner.toString();
    }
}
