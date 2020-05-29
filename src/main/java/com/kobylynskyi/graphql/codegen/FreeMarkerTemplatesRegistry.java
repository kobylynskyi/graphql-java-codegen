package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.UnableToLoadFreeMarkerTemplateException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;

class FreeMarkerTemplatesRegistry {

    static Template typeTemplate;
    static Template enumTemplate;
    static Template unionTemplate;
    static Template requestTemplate;
    static Template responseTemplate;
    static Template interfaceTemplate;
    static Template operationsTemplate;
    static Template parametrizedInputTemplate;
    static Template responseProjectionTemplate;

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
