package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.exception.UnableToLoadFreeMarkerTemplateException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.io.IOException;
import java.util.EnumMap;

import static com.kobylynskyi.graphql.codegen.FreeMarkerTemplateType.ENUM;
import static com.kobylynskyi.graphql.codegen.FreeMarkerTemplateType.INTERFACE;
import static com.kobylynskyi.graphql.codegen.FreeMarkerTemplateType.OPERATIONS;
import static com.kobylynskyi.graphql.codegen.FreeMarkerTemplateType.PARAMETRIZED_INPUT;
import static com.kobylynskyi.graphql.codegen.FreeMarkerTemplateType.REQUEST;
import static com.kobylynskyi.graphql.codegen.FreeMarkerTemplateType.RESPONSE;
import static com.kobylynskyi.graphql.codegen.FreeMarkerTemplateType.RESPONSE_PROJECTION;
import static com.kobylynskyi.graphql.codegen.FreeMarkerTemplateType.TYPE;
import static com.kobylynskyi.graphql.codegen.FreeMarkerTemplateType.UNION;

class FreeMarkerTemplatesRegistry {

    private static final Version FREEMARKER_TEMPLATE_VERSION = Configuration.VERSION_2_3_30;
    private static final EnumMap<GeneratedLanguage, EnumMap<FreeMarkerTemplateType, Template>> templateMap = new EnumMap<>(GeneratedLanguage.class);

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
            EnumMap<FreeMarkerTemplateType, Template> javaTemplates = new EnumMap<>(FreeMarkerTemplateType.class);
            javaTemplates.put(TYPE, configuration.getTemplate("templates/java-lang/javaClassGraphqlType.ftl"));
            javaTemplates.put(ENUM, configuration.getTemplate("templates/java-lang/javaClassGraphqlEnum.ftl"));
            javaTemplates.put(UNION, configuration.getTemplate("templates/java-lang/javaClassGraphqlUnion.ftl"));
            javaTemplates.put(REQUEST, configuration.getTemplate("templates/java-lang/javaClassGraphqlRequest.ftl"));
            javaTemplates.put(RESPONSE, configuration.getTemplate("templates/java-lang/javaClassGraphqlResponse.ftl"));
            javaTemplates.put(INTERFACE, configuration.getTemplate("templates/java-lang/javaClassGraphqlInterface.ftl"));
            javaTemplates.put(OPERATIONS, configuration.getTemplate("templates/java-lang/javaClassGraphqlOperations.ftl"));
            javaTemplates.put(PARAMETRIZED_INPUT, configuration.getTemplate("templates/java-lang/javaClassGraphqlParametrizedInput.ftl"));
            javaTemplates.put(RESPONSE_PROJECTION, configuration.getTemplate("templates/java-lang/javaClassGraphqlResponseProjection.ftl"));
            templateMap.put(GeneratedLanguage.JAVA, javaTemplates);

            EnumMap<FreeMarkerTemplateType, Template> scalaTemplates = new EnumMap<>(FreeMarkerTemplateType.class);
            scalaTemplates.put(TYPE, configuration.getTemplate("templates/scala-lang/scalaClassGraphqlType.ftl"));
            scalaTemplates.put(ENUM, configuration.getTemplate("templates/scala-lang/scalaClassGraphqlEnum.ftl"));
            scalaTemplates.put(UNION, configuration.getTemplate("templates/scala-lang/scalaClassGraphqlUnion.ftl"));
            scalaTemplates.put(REQUEST, configuration.getTemplate("templates/scala-lang/scalaClassGraphqlRequest.ftl"));
            scalaTemplates.put(RESPONSE, configuration.getTemplate("templates/scala-lang/scalaClassGraphqlResponse.ftl"));
            scalaTemplates.put(INTERFACE, configuration.getTemplate("templates/scala-lang/scalaClassGraphqlInterface.ftl"));
            scalaTemplates.put(OPERATIONS, configuration.getTemplate("templates/scala-lang/scalaClassGraphqlOperations.ftl"));
            scalaTemplates.put(PARAMETRIZED_INPUT, configuration.getTemplate("templates/scala-lang/scalaClassGraphqlParametrizedInput.ftl"));
            scalaTemplates.put(RESPONSE_PROJECTION, configuration.getTemplate("templates/scala-lang/scalaClassGraphqlResponseProjection.ftl"));
            templateMap.put(GeneratedLanguage.SCALA, scalaTemplates);

            EnumMap<FreeMarkerTemplateType, Template> kotlinTemplates = new EnumMap<>(FreeMarkerTemplateType.class);
            kotlinTemplates.put(TYPE, configuration.getTemplate("templates/kotlin-lang/kotlinClassGraphqlType.ftl"));
            kotlinTemplates.put(ENUM, configuration.getTemplate("templates/kotlin-lang/kotlinClassGraphqlEnum.ftl"));
            kotlinTemplates.put(UNION, configuration.getTemplate("templates/kotlin-lang/kotlinClassGraphqlUnion.ftl"));
            kotlinTemplates.put(REQUEST, configuration.getTemplate("templates/kotlin-lang/kotlinClassGraphqlRequest.ftl"));
            kotlinTemplates.put(RESPONSE, configuration.getTemplate("templates/kotlin-lang/kotlinClassGraphqlResponse.ftl"));
            kotlinTemplates.put(INTERFACE, configuration.getTemplate("templates/kotlin-lang/kotlinClassGraphqlInterface.ftl"));
            kotlinTemplates.put(OPERATIONS, configuration.getTemplate("templates/kotlin-lang/kotlinClassGraphqlOperations.ftl"));
            kotlinTemplates.put(PARAMETRIZED_INPUT, configuration.getTemplate("templates/kotlin-lang/kotlinClassGraphqlParametrizedInput.ftl"));
            kotlinTemplates.put(RESPONSE_PROJECTION, configuration.getTemplate("templates/kotlin-lang/kotlinClassGraphqlResponseProjection.ftl"));
            templateMap.put(GeneratedLanguage.KOTLIN, kotlinTemplates);
        } catch (IOException e) {
            throw new UnableToLoadFreeMarkerTemplateException(e);
        }
    }

    private FreeMarkerTemplatesRegistry() {
    }

    public static Template getTemplateWithLang(GeneratedLanguage generatedLanguage, FreeMarkerTemplateType templateType) {
        return templateMap.get(generatedLanguage).get(templateType);
    }

}
