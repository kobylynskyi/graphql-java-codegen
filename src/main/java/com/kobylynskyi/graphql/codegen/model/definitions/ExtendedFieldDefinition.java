package com.kobylynskyi.graphql.codegen.model.definitions;

import com.kobylynskyi.graphql.codegen.utils.Utils;
import graphql.language.Comment;
import graphql.language.Description;
import graphql.language.FieldDefinition;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to identify whether the field definition comes from the extension or base definition
 */
public class ExtendedFieldDefinition extends FieldDefinition {

    private final boolean fromExtension;

    protected ExtendedFieldDefinition(FieldDefinition f, boolean fromExtension) {
        super(f.getName(), f.getType(), f.getInputValueDefinitions(), f.getDirectives(),
                f.getDescription(), f.getSourceLocation(), f.getComments(), f.getIgnoredChars(),
                f.getAdditionalData());
        this.fromExtension = fromExtension;
    }

    /**
     * Get java doc of GraphQL field
     *
     * @return a list of Java docs for this GraphQL field
     */
    public List<String> getJavaDoc() {
        Description description = getDescription();
        if (description != null && Utils.isNotBlank(description.getContent())) {
            return Collections.singletonList(description.getContent().trim());
        }
        List<Comment> comments = getComments();
        if (comments == null) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(Comment::getContent).filter(Utils::isNotBlank)
                .map(String::trim).collect(Collectors.toList());

    }

    public boolean isFromExtension() {
        return fromExtension;
    }
}
