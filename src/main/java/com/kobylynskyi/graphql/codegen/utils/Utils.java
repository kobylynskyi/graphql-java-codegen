package com.kobylynskyi.graphql.codegen.utils;

import com.kobylynskyi.graphql.codegen.model.UnableToCreateDirectoryException;
import com.kobylynskyi.graphql.codegen.model.UnableToDeleteDirectoryException;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

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
        return typeDefNormalized.equals(GraphQLOperation.QUERY.name()) ||
                typeDefNormalized.equals(GraphQLOperation.MUTATION.name()) ||
                typeDefNormalized.equals(GraphQLOperation.SUBSCRIPTION.name());
    }

    /**
     * Capitalize a string. Make first letter as capital
     *
     * @param aString string to capitalize
     * @return capitalized string
     */
    public static String capitalize(String aString) {
        char[] chars = aString.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    /**
     * Uncapitalize a string. Make first letter as lowercase
     *
     * @param aString string to uncapitalize
     * @return uncapitalized string
     */
    public static String uncapitalize(String aString) {
        char[] chars = aString.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }

    /**
     * Inverted copy of org.apache.commons.lang3.StringUtils.isBlank(CharSequence cs)
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code false} if the CharSequence is null, empty or whitespace only
     */
    public static boolean isNotBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return true;
            }
        }
        return false;
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
     * @throws UnableToDeleteDirectoryException if unable to delete a directory
     */
    public static void deleteDir(File dir) {
        if (!dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File subFile : files) {
                if (subFile.isDirectory()) {
                    deleteDir(subFile);
                } else {
                    try {
                        Files.delete(subFile.toPath());
                    } catch (IOException e) {
                        throw new UnableToDeleteDirectoryException(e);
                    }
                }
            }
        }
        try {
            Files.delete(dir.toPath());
        } catch (IOException e) {
            throw new UnableToDeleteDirectoryException(e);
        }
    }

    /**
     * Create directory if it is absent. Will do nothing if it is already present.
     *
     * @param dir to create if it is absent.
     * @throws UnableToCreateDirectoryException if unable to create a directory
     */
    public static void createDirIfAbsent(File dir) {
        if (dir.exists()) {
            return;
        }
        boolean outputDirCreated = dir.mkdirs();
        if (!outputDirCreated) {
            throw new UnableToCreateDirectoryException(dir.getName());
        }
    }

    /**
     * Check if collection is empty.
     *
     * @param collection that will be checked for emptiness
     * @return true if collection is null or empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

}
