package com.kobylynskyi.graphql.codegen.model;

import lombok.Data;

@Data
public class ParentInterfacesConfig implements Combinable<ParentInterfacesConfig> {

    private String queryResolver;
    private String mutationResolver;
    private String subscriptionResolver;
    private String resolver;

    @Override
    public void combine(ParentInterfacesConfig source) {
        if (source == null) {
            return;
        }
        this.queryResolver = source.queryResolver != null ? source.queryResolver : this.queryResolver;
        this.mutationResolver = source.mutationResolver != null ? source.mutationResolver : this.mutationResolver;
        this.subscriptionResolver = source.subscriptionResolver != null ? source.subscriptionResolver : this.subscriptionResolver;
        this.resolver = source.resolver != null ? source.resolver : this.resolver;
    }

}
