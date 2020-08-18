package com.unicorn.utils;


import com.unicorn.base.logger.Logger;
import org.testng.Assert;

import javax.xml.bind.ValidationException;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The CommonUtils provides the utilities and functions which are commonly needed by all test cases and test steps
 * Created by Sagar Agarwal
 */

public class CommonUtils {

    private static final int RANDOM_STRING_LENGTH = 10;

    /**
     * Get current time stamp in the format 'yyyyMMddHHmmss'
     *
     * @return time stamp
     */
    public static String getCurrentTimeStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Generate 4 bit random number
     *
     * @return random number
     */
    public static String get4BitRandonNumber() {
        return String.format("%04d", new BigInteger(4, new Random()));
    }

    /**
     * Generate 8 bit random number
     *
     * @return random number
     */
    public static String get8BitRandonNumber() {
        return String.format("%08d", new BigInteger(8, new Random()));
    }

    /**
     * Generate 16 bit random number
     *
     * @return random number
     */
    public static String get16BitRandonNumber() {
        return String.format("%016d", new BigInteger(16, new Random()));
    }

    /**
     * Generate 32 bit random number
     *
     * @return random number
     */
    public static String get32BitRandonNumber() {
        return String.format("%032d", new BigInteger(32, new Random()));
    }

    /**
     * Generate 64 bit random number
     *
     * @return random number
     */
    public static String get64BitRandonNumber() {
        return String.format("%064d", new BigInteger(64, new Random()));
    }

    /**
     * Generate n bit random number
     *
     * @param number of bit
     * @return random number
     */
    public static String getNBitRandonNumber(int number) {
        String format = "%0" + number + "d";
        return String.format(format, new BigInteger(number, new Random()));
    }

    /**
     * Generates alpha-numeric random string
     *
     * @return random String
     */
    public static String getRandomString(int length) {
        String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuffer randStr = new StringBuffer();
        Random rnd = new Random();
        while (randStr.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHAR_LIST.length());
            randStr.append(CHAR_LIST.charAt(index));
        }
        return randStr.toString();
    }

    /**
     * This method generates random numeric string
     *
     * @return random String
     */
    public static String getRandomNumber(int length) {
        String CHAR_LIST = "1234567890";
        StringBuffer randStr = new StringBuffer();
        Random rnd = new Random();
        while (randStr.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHAR_LIST.length());
            randStr.append(CHAR_LIST.charAt(index));
        }
        return randStr.toString();
    }

    /**
     * Read properties file
     *
     * @param filePath filepath
     * @return properties
     * @throws Exception
     */
    public static Properties readPropertiesFile(String filePath) {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream(filePath);
            // load a properties file
            prop.load(input);
        } catch (IOException io) {
            Assert.fail(filePath + " is not found!", io);
        }
        return prop;
    }

    /**
     * Delete empty and non-empty directory
     *
     * @param dir directory path
     * @return boolean result
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    Logger.error("Failed to delete dir: " + dir.getAbsolutePath());
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * Verify if directory is empty
     *
     * @param directory directory path
     * @return boolean result
     */
    public static boolean isDirEmpty(final Path directory) {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
            return !dirStream.iterator().hasNext();
        } catch (IOException ioe) {
            Logger.error(ioe.toString());
        }
        return false;
    }

    /**
     * This method detects the os type and return it
     *
     * @return os type
     */
    public static String getOSType() throws Exception {
        String osType = System.getProperty("os.name").toLowerCase();
        if (osType.contains("windows")) {
            return "windows";
        } else if (osType.contains("linux")) {
            return "linux";
        } else if (osType.contains("mac")) {
            return "mac";
        } else {
            throw new ValidationException("Unsupported operation system!");
        }
    }

    /**
     * This find the available port on the local machine
     *
     * @return available port number
     * @throws IOException
     */
    public static Integer findOpenPortOnAllLocalInterfaces() {
        int port;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (Exception e) {
            Logger.error("Failed to find local open port !");
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * This will return the path of resource file or directory
     *
     * @param resourceName
     * @return
     */
    public static String getResourcePath(String resourceName) {
        return CommonUtils.class.getClassLoader().getResource(resourceName).getPath().trim();
    }

    public static void mergeFiles(File[] files, File mergedFile) {
        FileWriter fstream = null;
        BufferedWriter out = null;
        try {
            fstream = new FileWriter(mergedFile, true);
            out = new BufferedWriter(fstream);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for (File f : files) {
            Logger.info("Merging file: " + f.getName() + " with file: " + mergedFile.getName());
            FileInputStream fis;
            try {
                fis = new FileInputStream(f);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                String aLine;
                while ((aLine = in.readLine()) != null) {
                    out.write(aLine);
                    out.newLine();
                }
                in.close();
            } catch (IOException e) {
                Logger.assertFail("Exception while merging the files", e);
                e.printStackTrace();
            }
        }
        try {
            out.close();
        } catch (IOException e) {
            Logger.assertFail("Exception while merging the files", e);
            e.printStackTrace();
        }
        Logger.info("Successfully merged all files");
    }


    public static String getACLAIMPath(String executionEnvironment) {
        String aclaimPath = null;
        String location = "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Tanium\\Tanium Client\\Sensor Data\\Applications\\ACLAIM_" + executionEnvironment;
        String key = "Path";
        try {
        // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec("reg query " + '"' + location + "\" /v " + key);

            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
         // Parse out the value
            String[] parsed = reader.getResult().split("\\s+");
            if (parsed.length > 1) {
                aclaimPath = parsed[parsed.length - 1];
                aclaimPath = aclaimPath + "\\ACLAIM_" + executionEnvironment +".exe";
            }
        } catch (Exception e) {
        }
        return aclaimPath;
    }

    public static String getApplicationPath(String appName) {
        String appPath = null;
        String location = "HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\Tanium\\Tanium Client\\Sensor Data\\Applications\\"+appName;
        String key = "Path";
        try {
            // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec("reg query " + '"' + location + "\" /v " + key);

            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            // Parse out the value
            String[] parsed = reader.getResult().split("REG_SZ");
            if (parsed.length > 1) {
                appPath = parsed[parsed.length - 1];
                appPath = appPath.replaceAll("\r","").replaceAll("\n","").trim() + "\\"+appName+".exe";
                Logger.info("Application Path: "+appPath);
            }
        } catch (Exception e) {
        }
        return appPath;
    }


    public static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw = new StringWriter();

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            } catch (IOException e) {
            }
        }
        public String getResult() {
            return sw.toString();
        }
    }
}
