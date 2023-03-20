package com.kobylynskyi.graphql.codegen.generators;

import com.kobylynskyi.graphql.codegen.GraphQLCodegen;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.exception.UnableToLoadFreeMarkerTemplateException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.PlainTextOutputFormat;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;

class FreeMarkerTemplatesRegistry {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final Version FREEMARKER_TEMPLATE_VERSION = Configuration.VERSION_2_3_31;

    private static final EnumMap<GeneratedLanguage, EnumMap<FreeMarkerTemplateType, Template>> templateMap =
            new EnumMap<>(GeneratedLanguage.class);

    private static final Configuration configuration;
    
    static {
        try {
            configuration = buildFreeMarkerTemplateConfiguration();
            templateMap.put(GeneratedLanguage.JAVA, getTemplates(configuration, GeneratedLanguage.JAVA));
            templateMap.put(GeneratedLanguage.SCALA, getTemplates(configuration, GeneratedLanguage.SCALA));
            templateMap.put(GeneratedLanguage.KOTLIN, getTemplates(configuration, GeneratedLanguage.KOTLIN));
        } catch (IOException e) {
            throw new UnableToLoadFreeMarkerTemplateException(e);
        }
    }

    private FreeMarkerTemplatesRegistry() {
    }

    public static Template getTemplateWithLang(GeneratedLanguage generatedLanguage,
                                               FreeMarkerTemplateType templateType) {
        return templateMap.get(generatedLanguage).get(templateType);
    }

    private static EnumMap<FreeMarkerTemplateType, Template> getTemplates(Configuration configuration,
                                                                          GeneratedLanguage language)
            throws IOException {
        EnumMap<FreeMarkerTemplateType, Template> templates = new EnumMap<>(FreeMarkerTemplateType.class);
        for (FreeMarkerTemplateType templateType : FreeMarkerTemplateType.values()) {
            templates.put(templateType, configuration.getTemplate(buildTemplatePath(templateType, language)));
        }
        return templates;
    }

    private static String buildTemplatePath(FreeMarkerTemplateType templateType, GeneratedLanguage language) {
        return String.format("templates/%s-lang/%s.ftl",
                language.name().toLowerCase(),
                templateType.name().toLowerCase());
    }

    private static Configuration buildFreeMarkerTemplateConfiguration() throws IOException {
        Configuration configuration = new Configuration(FREEMARKER_TEMPLATE_VERSION);
        ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(GraphQLCodegen.class.getClassLoader(), "");
        FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(new File("."));
        configuration.setTemplateLoader(
                new MultiTemplateLoader(new TemplateLoader[] {classTemplateLoader, fileTemplateLoader}));
        configuration.setDefaultEncoding(DEFAULT_ENCODING);
        configuration.setOutputFormat(PlainTextOutputFormat.INSTANCE);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setSharedVariable("statics", new BeansWrapper(FREEMARKER_TEMPLATE_VERSION).getStaticModels());
        return configuration;
    }

    public static Template getCustomTemplates(String templatePath) {
        try {
            return configuration.getTemplate(templatePath);
        } catch (IOException e) {
            throw new UnableToLoadFreeMarkerTemplateException(e);
        }
    }

}
