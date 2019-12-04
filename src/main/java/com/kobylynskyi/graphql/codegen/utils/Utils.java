package com.kobylynskyi.graphql.codegen.utils;

import com.google.gson.GsonBuilder;
import com.kobylynskyi.graphql.codegen.model.MappingConfig;
import graphql.language.OperationDefinition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Various utilities
 *
 * @author kobylynskyi
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Check whether the given type definition is either Query or Mutation or Subscription.
     *
     * @param typeDef type definition name
     * @return {@code true} if the given type definition is GraphQL operation
     */
    public static boolean isGraphqlOperation(String typeDef) {
        String typeDefNormalized = typeDef.toUpperCase();
        return typeDefNormalized.equals(OperationDefinition.Operation.QUERY.name()) ||
                typeDefNormalized.equals(OperationDefinition.Operation.MUTATION.name()) ||
                typeDefNormalized.equals(OperationDefinition.Operation.SUBSCRIPTION.name());
    }

    /**
     * Capitalize a string. Make first letter as capital
     *
     * @param aString string to capitalize
     * @return capitalized string
     */
    public static String capitalize(String aString) {
        if (Utils.isBlank(aString)) {
            return aString;
        }
        char[] chars = aString.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * Basically copy of org.apache.commons.lang3.StringUtils.isBlank(CharSequence cs)
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get content of the file.
     *
     * @param filePath path of the file.
     * @return content of the file.
     * @throws IOException unable to read the file.
     */
    public static String getFileContent(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Deletes a given directory recursively.
     *
     * @param dir directory to delete
     * @throws IOException unable to delete a directory
     */
    public static void deleteDir(File dir) throws IOException {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File subFile : files) {
                if (subFile.isDirectory()) {
                    deleteDir(subFile);
                } else {
                    Files.delete(subFile.toPath());
                }
            }
        }
        Files.delete(dir.toPath());
    }

    /**
     * Create directory if it is absent. Will do nothing if it is already present.
     *
     * @param dir to create if it is absent.
     * @throws IOException if unable to create a directory
     */
    public static void createDirIfAbsent(File dir) throws IOException {
        if (dir.exists()) {
            return;
        }
        boolean outputDirCreated = dir.mkdirs();
        if (!outputDirCreated) {
            throw new IOException("Unable to create output directory");
        }
    }


}
