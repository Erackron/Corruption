package com.mcdr.corruption.util;

import java.util.regex.Pattern;

/**
 * This class is used to group utility method that have to do with version numbers.
 */
public abstract class VersionUtil {

    /**
     * Checks if the first input is an older version number than the second input
     *
     * @param version1 the version to check
     * @param version2 the version to check against
     * @return true if the first input is an older version number, false if it isn't
     */
    public static boolean isOlderVersion(String version1, String version2) {
        return isNewerVersion(version2, version1);
    }

    /**
     * Checks if the first input is a newer version number than the second input
     *
     * @param version1 the version to check
     * @param version2 the version to check against
     * @return true if the first input is a newer version number, false if it isn't
     */
    public static boolean isNewerVersion(String version1, String version2) {
        String s1 = normalisedVersion(version1);
        String s2 = normalisedVersion(version2);
        int cmp = s1.compareTo(s2);
        //String cmpStr = cmp < 0 ? "<" : cmp > 0 ? ">" : "==";
        return cmp > 0;
    }

    /**
     * Normalise a version number with a dot (.) as seperator and a character width of 4.
     *
     * @param version The version string to normalise
     * @return The normalised version string
     */
    private static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    /**
     * Normalise a version number.
     *
     * @param version  The version string to normalise
     * @param sep      The seperator for the different version parts
     * @param maxWidth The maximum character width of each version part
     * @return The normalised version string
     */
    private static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }
}
