package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.exception.UnableToLoadFreeMarkerTemplateException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.io.IOException;

class FreeMarkerTemplatesRegistry {

    private static final Version FREEMARKER_TEMPLATE_VERSION = Configuration.VERSION_2_3_30;

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
        BeansWrapper beansWrapper = new BeansWrapper(FREEMARKER_TEMPLATE_VERSION);
        Configuration configuration = new Configuration(FREEMARKER_TEMPLATE_VERSION);
        configuration.setClassLoaderForTemplateLoading(GraphQLCodegen.class.getClassLoader(), "");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setSharedVariable("statics", beansWrapper.getStaticModels());

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

    private FreeMarkerTemplatesRegistry() {
    }

}
