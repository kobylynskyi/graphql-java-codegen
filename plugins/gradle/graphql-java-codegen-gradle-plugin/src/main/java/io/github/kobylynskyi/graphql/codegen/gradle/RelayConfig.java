package io.github.kobylynskyi.graphql.codegen.gradle;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

public class RelayConfig extends com.kobylynskyi.graphql.codegen.model.RelayConfig {

    @Input
    @Optional
    @Override
    public String getDirectiveName() {
        return super.getDirectiveName();
    }

    @Input
    @Optional
    @Override
    public String getDirectiveArgumentName() {
        return super.getDirectiveArgumentName();
    }

    @Input
    @Optional
    @Override
    public String getConnectionType() {
        return super.getConnectionType();
    }

}
