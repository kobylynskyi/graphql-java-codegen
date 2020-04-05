package io.github.kobylynskyi.graphql.codegen.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

/**
 * Gradle plugin for GraphQL code generation
 *
 * @author kobylynskyi
 */
public class GraphqlCodegenGradlePlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        TaskContainer tasks = project.getTasks();
        tasks.create("graphqlCodegen", GraphqlCodegenGradleTask.class);
        tasks.create("graphqlCodegenValidate", GraphqlCodegenValidateGradleTask.class);
    }

}
