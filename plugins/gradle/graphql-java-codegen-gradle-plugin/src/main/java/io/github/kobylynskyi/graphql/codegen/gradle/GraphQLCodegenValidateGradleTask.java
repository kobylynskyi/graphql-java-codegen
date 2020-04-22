package io.github.kobylynskyi.graphql.codegen.gradle;

import com.kobylynskyi.graphql.codegen.GraphQLCodegenValidate;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.List;

/**
 * Gradle task for GraphQL code generation
 *
 * @author kobylynskyi
 */
public class GraphQLCodegenValidateGradleTask extends DefaultTask {

    private List<String> graphqlSchemaPaths;

    @TaskAction
    public void validate() throws IOException {
        new GraphQLCodegenValidate(graphqlSchemaPaths).validate();
    }

    @Input
    public List<String> getGraphqlSchemaPaths() {
        return graphqlSchemaPaths;
    }

    public void setGraphqlSchemaPaths(List<String> graphqlSchemaPaths) {
        this.graphqlSchemaPaths = graphqlSchemaPaths;
    }
}
