package io.github.kobylynskyi.graphql.codegen;

/**
 * Configuration of parent interfaces to be used during code generation
 */
public class ParentInterfacesConfig {

    private String queryResolver;
    private String mutationResolver;
    private String subscriptionResolver;
    private String resolver;

    public String getQueryResolver() {
        return queryResolver;
    }

    public void setQueryResolver(String queryResolver) {
        this.queryResolver = queryResolver;
    }

    public String getMutationResolver() {
        return mutationResolver;
    }

    public void setMutationResolver(String mutationResolver) {
        this.mutationResolver = mutationResolver;
    }

    public String getSubscriptionResolver() {
        return subscriptionResolver;
    }

    public void setSubscriptionResolver(String subscriptionResolver) {
        this.subscriptionResolver = subscriptionResolver;
    }

    public String getResolver() {
        return resolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

}
