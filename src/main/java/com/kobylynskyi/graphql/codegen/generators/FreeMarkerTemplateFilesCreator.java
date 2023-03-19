package com.kobylynskyi.graphql.codegen.generators;

import com.kobylynskyi.graphql.codegen.model.DataModelFields;
import com.kobylynskyi.graphql.codegen.model.GeneratedLanguage;
import com.kobylynskyi.graphql.codegen.model.MappingContext;
import com.kobylynskyi.graphql.codegen.model.exception.UnableToCreateFileException;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;

/**
 * Utility class for generating files
 *
 * @author kobylynskyi
 */
public class FreeMarkerTemplateFilesCreator {

    private FreeMarkerTemplateFilesCreator() {
    }

    /**
     * Crates a new file according to template
     *
     * @param mappingContext Global mapping context
     * @param templateType   Template type
     * @param dataModel      FreeMarker data model
     * @return a created file
     */
    public static File create(MappingContext mappingContext,
                              FreeMarkerTemplateType templateType,
                              Map<String, Object> dataModel) {
        GeneratedLanguage language = mappingContext.getGeneratedLanguage();
        String fileName = dataModel.get(DataModelFields.CLASS_NAME) + language.getFileExtension();
        File fileOutputDir = getFileTargetDirectory(dataModel, mappingContext.getOutputDirectory());
        File javaSourceFile = new File(fileOutputDir, fileName);

        try {
            if (!javaSourceFile.createNewFile()) {
                throw new FileAlreadyExistsException("File already exists: " + javaSourceFile.getPath());
            }
        } catch (IOException e) {
            throw new UnableToCreateFileException(e);
        }

        try (FileWriter fileWriter = new FileWriter(javaSourceFile)) {
            Template template = getTemplateForTypeAndLanguage(mappingContext, templateType, language);
            template.process(dataModel, fileWriter);
        } catch (Exception e) {
            throw new UnableToCreateFileException(e);
        }
        return javaSourceFile;
    }

    private static Template getTemplateForTypeAndLanguage(MappingContext mappingContext,
                                                          FreeMarkerTemplateType templateType,
                                                          GeneratedLanguage language) {
        String templatePath = null;
        if (mappingContext.getCustomTemplates() != null) {
            templatePath = mappingContext.getCustomTemplates().get(templateType);
        }
        if (templatePath != null) {
            return FreeMarkerTemplatesRegistry.getCustomTemplates(templatePath);
        } else {
            return FreeMarkerTemplatesRegistry.getTemplateWithLang(language, templateType);
        }
    }

    private static File getFileTargetDirectory(Map<String, Object> dataModel, File outputDir) {
        File targetDir;
        Object packageName = dataModel.get(DataModelFields.PACKAGE);
        if (packageName != null && Utils.isNotBlank(packageName.toString())) {
            targetDir = new File(outputDir, packageName.toString().replace(".", File.separator));
        } else {
            targetDir = outputDir;
        }
        Utils.createDirIfAbsent(targetDir);
        return targetDir;
    }

}
