package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.UnableToLoadFreeMarkerTemplateException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;

class FreeMarkerTemplatesRegistry {

    static final Template typeTemplate;
    static final Template enumTemplate;
    static final Template unionTemplate;
    static final Template requestTemplate;
    static final Template responseTemplate;
    static final Template interfaceTemplate;
    static final Template operationsTemplate;
    static final Template parametrizedInputTemplate;
    static final Template responseProjectionTemplate;

    static {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setClassLoaderForTemplateLoading(GraphQLCodegen.class.getClassLoader(), "");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);

        try {
            typeTemplate = configuration.getTemplate("templates/javaClassGraphqlType.ftl");
            enumTemplate = configuration.getTemplate("templates/javaClassGraphqlEnum.ftl");
            unionTemplate = configuration.getTemplate("templates/javaClassGraphqlUnion.ftl");
            requestTemplate = configuration.getTemplate("templates/javaClassGraphqlRequest.ftl");
            responseTemplate = configuration.getTemplate("templates/javaClassGraphqlResponse.ftl");
            interfaceTemplate = configuration.getTemplate("templates/javaClassGraphqlInterface.ftl");
            operationsTemplate = configuration.getTemplate("templates/javaClassGraphqlOperations.ftl");
            parametrizedInputTemplate = configuration.getTemplate("templates/javaClassGraphqlParametrizedInput.ftl");
            responseProjectionTemplate = configuration.getTemplate("templates/javaClassGraphqlResponseProjection.ftl");
        } catch (IOException e) {
            throw new UnableToLoadFreeMarkerTemplateException(e);
        }
    }
}
