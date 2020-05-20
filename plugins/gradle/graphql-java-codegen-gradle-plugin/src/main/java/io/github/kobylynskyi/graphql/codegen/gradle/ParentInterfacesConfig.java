package io.github.kobylynskyi.graphql.codegen.gradle;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

public class ParentInterfacesConfig {

    private String queryResolver;
    private String mutationResolver;
    private String subscriptionResolver;
    private String resolver;

    @Input
    @Optional
    public String getQueryResolver() {
        return queryResolver;
    }

    @Input
    @Optional
    public String getMutationResolver() {
        return mutationResolver;
    }

    @Input
    @Optional
    public String getSubscriptionResolver() {
        return subscriptionResolver;
    }

    @Input
    @Optional
    public String getResolver() {
        return resolver;
    }

    public void setQueryResolver(String queryResolver) {
        this.queryResolver = queryResolver;
    }

    public void setMutationResolver(String mutationResolver) {
        this.mutationResolver = mutationResolver;
    }

    public void setSubscriptionResolver(String subscriptionResolver) {
        this.subscriptionResolver = subscriptionResolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

}
