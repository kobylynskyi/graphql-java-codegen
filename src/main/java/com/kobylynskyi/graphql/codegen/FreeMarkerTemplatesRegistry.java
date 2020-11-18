package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.exception.UnableToLoadFreeMarkerTemplateException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class FreeMarkerTemplatesRegistry {

    private static final Version FREEMARKER_TEMPLATE_VERSION = Configuration.VERSION_2_3_30;
    private static final Map<String, Map<String, Template>> templateMap = new HashMap<>();

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
            Map<String, Template> javaTemplates = new HashMap<>();
            javaTemplates.put("typeTemplate", configuration.getTemplate("templates/javaClassGraphqlType.ftl"));
            javaTemplates.put("enumTemplate", configuration.getTemplate("templates/javaClassGraphqlEnum.ftl"));
            javaTemplates.put("unionTemplate", configuration.getTemplate("templates/javaClassGraphqlUnion.ftl"));
            javaTemplates.put("requestTemplate", configuration.getTemplate("templates/javaClassGraphqlRequest.ftl"));
            javaTemplates.put("responseTemplate", configuration.getTemplate("templates/javaClassGraphqlResponse.ftl"));
            javaTemplates.put("interfaceTemplate", configuration.getTemplate("templates/javaClassGraphqlInterface.ftl"));
            javaTemplates.put("operationsTemplate", configuration.getTemplate("templates/javaClassGraphqlOperations.ftl"));
            javaTemplates.put("parametrizedInputTemplate", configuration.getTemplate("templates/javaClassGraphqlParametrizedInput.ftl"));
            javaTemplates.put("responseProjectionTemplate", configuration.getTemplate("templates/javaClassGraphqlResponseProjection.ftl"));
            templateMap.put(GeneratedLanguage.JAVA.name(), javaTemplates);

            Map<String, Template> scalaTemplates = new HashMap<>();
            scalaTemplates.put("typeTemplate", configuration.getTemplate("templates/scala-lang/scalaClassGraphqlType.ftl"));
            scalaTemplates.put("enumTemplate", configuration.getTemplate("templates/scala-lang/scalaClassGraphqlEnum.ftl"));
            scalaTemplates.put("unionTemplate", configuration.getTemplate("templates/scala-lang/scalaClassGraphqlUnion.ftl"));
            scalaTemplates.put("requestTemplate", configuration.getTemplate("templates/scala-lang/scalaClassGraphqlRequest.ftl"));
            scalaTemplates.put("responseTemplate", configuration.getTemplate("templates/scala-lang/scalaClassGraphqlResponse.ftl"));
            scalaTemplates.put("interfaceTemplate", configuration.getTemplate("templates/scala-lang/scalaClassGraphqlInterface.ftl"));
            scalaTemplates.put("operationsTemplate", configuration.getTemplate("templates/scala-lang/scalaClassGraphqlOperations.ftl"));
            scalaTemplates.put("parametrizedInputTemplate", configuration.getTemplate("templates/scala-lang/scalaClassGraphqlParametrizedInput.ftl"));
            scalaTemplates.put("responseProjectionTemplate", configuration.getTemplate("templates/scala-lang/scalaClassGraphqlResponseProjection.ftl"));
            templateMap.put(GeneratedLanguage.SCALA.name(), scalaTemplates);


        } catch (IOException e) {
            throw new UnableToLoadFreeMarkerTemplateException(e);
        }
    }

    private FreeMarkerTemplatesRegistry() {
    }

    public static Template getTemplateWithLang(GeneratedLanguage generatedLanguage, String templateName) {
        return templateMap.get(generatedLanguage.name()).get(templateName);
    }

}
