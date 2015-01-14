package com.mcdr.corruption.util;

import java.io.*;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is used to group utility methods that have to do with file operations.
 */
public abstract class FileUtil {

    /**
     * Copy sourceFile to destFile.
     *
     * @param sourceFile The file to copy the contents of
     * @param destFile   The file object to write the contents of sourceFile to
     * @throws IOException When any of the file operations fail
     */
    public static void fileToFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(destFile);
            source = fis.getChannel();
            destination = fos.getChannel();

            // previous code: destination.transferFrom(source, 0, source.size());
            // to avoid infinite loops, should be:
            long count = 0;
            long size = source.size();
            while ((count += destination.transferFrom(source, count, size - count)) < size) ;
        } finally {
            if (source != null)
                source.close();
            if (destination != null)
                destination.close();
            if (fis != null)
                fis.close();
            if (fos != null)
                fos.close();
        }
    }

    /**
     * Write the contents of an InputStream to a file.
     *
     * @param resource The InputStream to use the contents of
     * @param file     The file to write the contents of resource to
     * @throws IOException When any of the file operations fail
     */
    public static void streamToFile(InputStream resource, File file) throws IOException {
        if (!file.exists())
            file.createNewFile();

        OutputStream outputStream = new FileOutputStream(file);

        copy(resource, outputStream);
    }

    /**
     * Write the contents of an InputStream to an OutputStream.
     *
     * @param inputStream  The InputStream to use the contents of
     * @param outputStream The OutputStream to write the contents of inputStream to
     * @throws IOException When any of the IO operations fail
     */
    private static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        int read;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1)
            outputStream.write(bytes, 0, read);

        inputStream.close();
        outputStream.close();
    }

    /**
     * Calculate the md5 hash of a given file.
     *
     * @param f The file to get the md5 hash of
     * @return A string containing the md5 hash of the given file
     * @throws RuntimeException When the file cannot be read, doesn't exist
     *                          or when the md5 hashing algorithm wasn't found on the system.
     */
    public static String calculateMd5Hash(File f) throws RuntimeException {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            InputStream is = new FileInputStream(f);
            byte[] buffer = new byte[8192];
            int read;
            try {
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
                byte[] md5sum = digest.digest();
                BigInteger bigInt = new BigInteger(1, md5sum);
                return bigInt.toString(16);
            } catch (IOException e) {
                throw new RuntimeException("Unable to process file for MD5", e);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("The md5 algorithm doesn't seem to be available on your system", e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("The file to hash was not found, are you sure you have the right File object?", e);
        }
    }
}
