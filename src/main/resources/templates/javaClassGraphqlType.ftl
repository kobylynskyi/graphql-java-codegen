<#if package?has_content>
package ${package};

</#if>
<#list imports as import>
import ${import}.*;
</#list>

public class ${className} <#if implements?has_content>implements <#list implements as interface>${interface}<#if interface_has_next>, </#if></#list></#if>{

<#list fields as field>
<#list field.annotations as annotation>
    @${annotation}
</#list>
    private ${field.type} ${field.name};
</#list>

    public ${className}() {
    }

<#if fields?has_content>
    public ${className}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>) {
<#list fields as field>
        this.${field.name} = ${field.name};
</#list>
    }
</#if>

<#list fields as field>
    public ${field.type} get${field.name?cap_first}() {
        return ${field.name};
    }
    public void set${field.name?cap_first}(${field.type} ${field.name}) {
        this.${field.name} = ${field.name};
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
        return <#list fields as field>Objects.equals(${field.name}, that.${field.name})<#if field_has_next>
            && </#if></#list>;
    }

    @Override
    public int hashCode() {
<#if fields?has_content>
        return Objects.hash(<#list fields as field>${field.name}<#if field_has_next>, </#if></#list>);
<#else>
        return 0;
</#if>
    }
</#if>
<#if toString>
    @Override
    public String toString() {
        return "${className}{"
<#if fields?has_content>
<#list fields as field>
            + "${field.name}='" + ${field.name} + "'<#if field_has_next>,</#if>"
</#list>
</#if>
            + "}";
    }
</#if>
}