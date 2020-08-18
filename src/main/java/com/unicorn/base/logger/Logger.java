package com.unicorn.base.logger;

import com.unicorn.utils.CommonUtils;
import cucumber.api.testng.AbstractTestNGCucumberTests;
import org.apache.log4j.*;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.File;

/**
 * The Logger provides the logging facility
 *
 */

public class Logger extends AbstractTestNGCucumberTests {


    public static org.apache.log4j.Logger logger;
    private static boolean isLoggingDone = false;
    private FileAppender fAppender;

    /**
     * This will setup the class level logger and create the log file by class name of test case.
     */
    @BeforeSuite(alwaysRun = true)
    public void setupLogger() {
        if (!isLoggingDone) {
            CommonUtils.deleteDir(new File(System.getProperty("user.dir") + File.separator + "target"));
            logger = org.apache.log4j.Logger.getLogger(Logger.class);
            logger.setLevel(Level.INFO);
            logger.setLevel(Level.ERROR);
            logger.setLevel(Level.DEBUG);
            PatternLayout layout = new PatternLayout("%d{dd MMM yyyy HH:mm:ss} %-5p %x - %m%n");
            logger.addAppender(new ConsoleAppender(layout));
            File logFile = null;
            try {
                try {
                    logFile = new File("target" + File.separator + "logs" + File.separator + "automation" + ".log");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                fAppender = new RollingFileAppender(layout, logFile.getAbsolutePath());
                System.out.println("Path for log file: " + logFile.getAbsolutePath());

                logger.addAppender(fAppender);


            } catch (Exception e) {
                System.out.println("Failed to created logger ..  !!!");
                e.printStackTrace();
            } finally {
                isLoggingDone = true;
            }
        }
    }

    /**
     * This will reset the logger
     */
    @AfterSuite(alwaysRun = true)
    public void resetLogger() {
        System.out.println("Logger reseted");
        isLoggingDone = false;
    }

    /**
     * This tale the customized properties file and pass to the ScreenRecordingManager
     *
     * @param configFilePath properties file
     */

    /**
     * log info message to info file
     *
     * @param infoMessage info message
     */
    public static void info(String infoMessage) {
        logger.info(infoMessage);
    }


    public static void info(String message, String... parameter) {
        logger.info(String.format(message, (Object[]) parameter));
    }

    /**
     * log info message and exception stack trace to log file
     *
     * @param infoMessage info message
     * @param ex          exception object
     */
    public static void info(String infoMessage, Exception ex) {
        logger.info(infoMessage, ex);
    }

    /**
     * Log error message to log file
     *
     * @param errorMessage error message
     */
    public static void error(String errorMessage) {
        logger.error(errorMessage);
    }

    /**
     * Log error message and exception stack trace to log file
     *
     * @param errorMessage    error message
     * @param exceptionObject exception object
     */
    public static void error(String errorMessage, Exception exceptionObject) {
        logger.error(errorMessage, exceptionObject);
    }

    /**
     * Log debug message to log file
     *
     * @param debugMessage debug message
     */
    public static void debug(String debugMessage) {
        logger.debug(debugMessage);
    }

    /**
     * Log debug message and exception stack trace to log file
     *
     * @param debugMessage    debug message
     * @param exceptionObject exception object
     */
    public static void debug(String debugMessage, Exception exceptionObject) {
        logger.debug(debugMessage, exceptionObject);
        System.out.println(debugMessage);
    }

    /**
     * Log debug message to log file
     *
     * @param warnMessage warning message
     */
    public static void warning(String warnMessage) {
        logger.warn(warnMessage);
    }

    /**
     * Log fatal message and exception stack trace to log file
     *
     * @param fatalMessage    fatal message
     * @param exceptionObject exception object
     */
    public static void fatal(String fatalMessage, Exception exceptionObject) {
        logger.fatal(fatalMessage, exceptionObject);
    }

    /**
     * Log fatal message to log file
     *
     * @param fatalMessage fatal message
     */
    public static void fatal(String fatalMessage) {
        logger.fatal(fatalMessage);
    }

    /**
     * Log warning message and exception stack trace to log file
     *
     * @param warnMessage     warning message
     * @param exceptionObject exception object
     */
    public static void warning(String warnMessage, Exception exceptionObject) {
        logger.warn(warnMessage, exceptionObject);
    }


    /**
     * This method will log error message and will fail the execution
     *
     * @param logMessage message to be logged
     */
    public static void assertFail(String logMessage) {
        error(logMessage);
        Assert.fail(logMessage);
    }

    /**
     * This method will log error message and stack trace of exception and will fail the execution
     *
     * @param logMessage      message to be logged
     * @param exceptionObject Exception object
     */
    public static void assertFail(String logMessage, Exception exceptionObject) {
        error(logMessage, exceptionObject);
        Assert.fail(logMessage);
    }

    public static void assertFail(String message, String... parameters) {
        error(message);
        Assert.fail(String.format(message,  (Object[]) parameters));
    }

}


