package io.github.kobylynskyi.graphql.codegen;

public class ParentInterfacesConfig {

    private String queryResolver;
    private String mutationResolver;
    private String subscriptionResolver;
    private String resolver;

    public String getQueryResolver() {
        return queryResolver;
    }

    public String getMutationResolver() {
        return mutationResolver;
    }

    public String getSubscriptionResolver() {
        return subscriptionResolver;
    }

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
