package com.kobylynskyi.graphql.codegen.model.definitions;

import graphql.language.Node;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectTypeExtensionDefinition;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ExtendedObjectTypeDefinition extends ExtendedImplementingTypeDefinition<ObjectTypeDefinition, ObjectTypeExtensionDefinition> {

    /**
     * Get source location of the node.
     * In some cases Node does not have a source location defined, so returning "unknown"
     *
     * @return source location if present or "unknown" otherwise
     */
    private static String getSourceLocationName(Node<?> node) {
        return node.getSourceLocation() != null ? node.getSourceLocation().getSourceName() : "unknown";
    }

    public List<ExtendedFieldDefinition> getFieldDefinitions() {
        List<ExtendedFieldDefinition> definitions = new ArrayList<>();
        if (definition != null) {
            definition.getFieldDefinitions().stream()
                    .map(f -> new ExtendedFieldDefinition(f, false))
                    .forEach(definitions::add);
        }
        extensions.stream()
                .map(ObjectTypeExtensionDefinition::getFieldDefinitions)
                .flatMap(Collection::stream)
                .map(f -> new ExtendedFieldDefinition(f, true))
                .forEach(definitions::add);
        return definitions;
    }

    /**
     * Get definition and its extensions grouped by source location.
     *
     * @return {@link HashMap} where the key is definition SourceLocation
     * and value is a list of {@link ExtendedObjectTypeDefinition}s
     */
    public Map<String, ExtendedObjectTypeDefinition> groupBySourceLocationFile() {
        return groupBySourceLocation(File::getName);
    }

    public Map<String, ExtendedObjectTypeDefinition> groupBySourceLocationFolder() {
        return groupBySourceLocation(File::getParent);
    }

    private Map<String, ExtendedObjectTypeDefinition> groupBySourceLocation(Function<File, String> fileStringFunction) {
        Map<String, ExtendedObjectTypeDefinition> definitionMap = new HashMap<>();
        if (definition != null) {
            File file = new File(getSourceLocationName(definition));
            definitionMap.computeIfAbsent(fileStringFunction.apply(file), d -> new ExtendedObjectTypeDefinition())
                    .setDefinition(definition);
        }
        for (ObjectTypeExtensionDefinition extension : extensions) {
            File file = new File(getSourceLocationName(extension));
            definitionMap.computeIfAbsent(fileStringFunction.apply(file), d -> new ExtendedObjectTypeDefinition())
                    .getExtensions().add(extension);
        }
        return definitionMap;
    }

}
