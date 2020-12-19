package com.kobylynskyi.graphql.codegen.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kobylynskyi.graphql.codegen.model.exception.UnableToCreateDirectoryException;
import com.kobylynskyi.graphql.codegen.model.exception.UnableToDeleteDirectoryException;
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLOperation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;

/**
 * Various utilities
 *
 * @author kobylynskyi
 */
public final class Utils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    public static String capitalizeString(String aString) {
        char[] chars = aString.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '-' || chars[i] == '_' || chars[i] == '.') {
                found = false;
            }
        }
        return String.valueOf(chars);
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
     * Copy of org.apache.commons.lang3.StringUtils#isBlank(CharSequence)
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace only
     */
    public static boolean isBlank(final CharSequence cs) {
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
     * Check if strings are equal (consider blank strings equal to null)
     *
     * @param a a string
     * @param b a string to be compared with {@code a}
     * @return {@code true} if strings are equal
     */
    public static boolean stringsEqualIgnoreSpaces(String a, String b) {
        return Objects.equals(a, b) || (isBlank(a) && isBlank(b));
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
        try {
            Files.createDirectories(dir.toPath());
        } catch (IOException e) {
            throw new UnableToCreateDirectoryException(dir.getName(), e);
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

    /**
     * Replace leading annotation (@) sign
     *
     * @param value annotation value with/without @ sign
     * @return value without leading @ sign
     */
    public static String replaceLeadingAtSign(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("^@+", "");
    }

    /**
     * Copy of org.apache.commons.lang3.StringUtils#substringBetween(String, String, String)
     * <p>
     * Gets the String that is nested in between two Strings.
     * Only the first match is returned.
     *
     * @param str   the String containing the substring, may be null
     * @param open  the String before the substring, may be null
     * @param close the String after the substring, may be null
     * @return the substring, {@code null} if no match
     */
    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    /**
     * Wrap string
     *
     * @param str      the String
     * @param wrapWith String to be appended to the beginning and the end of <b>str</b>
     * @return string
     */
    public static String wrapString(String str, String wrapWith) {
        return wrapString(str, wrapWith, wrapWith);
    }

    /**
     * Wrap string
     *
     * @param str       the String
     * @param wrapStart String to be appended to the beginning of <b>str</b>
     * @param wrapEnd   String to be appended to the end of <b>str</b>
     * @return wrapped string
     */
    public static String wrapString(String str, String wrapStart, String wrapEnd) {
        if (str == null || wrapStart == null || wrapEnd == null) {
            return str;
        }
        return wrapStart + str + wrapEnd;
    }

}
