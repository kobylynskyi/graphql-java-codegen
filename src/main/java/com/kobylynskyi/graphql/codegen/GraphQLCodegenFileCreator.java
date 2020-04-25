package com.kobylynskyi.graphql.codegen;

import com.kobylynskyi.graphql.codegen.model.DataModelFields;
import com.kobylynskyi.graphql.codegen.model.UnableToCreateFileException;
import com.kobylynskyi.graphql.codegen.utils.Utils;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.FileAlreadyExistsException;
import java.util.Map;

/**
 * Utility class for generating files
 *
 * @author kobylynskyi
 */
class GraphQLCodegenFileCreator {

    private static final String EXTENSION = ".java";

    static File generateFile(Template template, Map<String, Object> dataModel, File outputDir) {
        String fileName = dataModel.get(DataModelFields.CLASS_NAME) + EXTENSION;
        File fileOutputDir = getFileTargetDirectory(dataModel, outputDir);
        File javaSourceFile = new File(fileOutputDir, fileName);
        try {
            if (!javaSourceFile.createNewFile()) {
                throw new FileAlreadyExistsException("File already exists: " + javaSourceFile.getPath());
            }
            template.process(dataModel, new FileWriter(javaSourceFile));
        } catch (Exception e) {
            throw new UnableToCreateFileException(e);
        }
        return javaSourceFile;
    }

    static void prepareOutputDir(File outputDir) {
        Utils.deleteDir(outputDir);
        Utils.createDirIfAbsent(outputDir);
    }

    private static File getFileTargetDirectory(Map<String, Object> dataModel, File outputDir) {
        File targetDir;
        Object packageName = dataModel.get(DataModelFields.PACKAGE);
        if (packageName != null && !Utils.isBlank(packageName.toString())) {
            targetDir = new File(outputDir, packageName.toString().replace(".", File.separator));
        } else {
            targetDir = outputDir;
        }
        Utils.createDirIfAbsent(targetDir);
        return targetDir;
    }

}
