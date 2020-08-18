package com.unicorn.utils;

import com.unicorn.base.logger.Logger;

import java.io.File;
import java.util.Properties;

public class AutProperties {
    private static String requestTimeOut;
    private static String appInstalledLocation;
    private static String windowsServerLocation;
    private static String isToUpdateJira;
    private static String jiraTestCycleName;
    private static String isToSetJiraProxy;
    private static String testFilter;
    private static String envConfigFile;
    private static String executionEnvironment;
    private static String appName;
    private static String aut;
    private static String emailTitle;
    private static String emailSignatureName;
    private static String emailSignatureId;
    public static Properties autProperties;

    public static void initAutProperties() {
        System.out.println("Initializing Application Properties");
        Properties commonProperties = PropertiesReader.readProperties(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "properties" + File.separator + "common" + File.separator + "common.properties");
        Properties emailProperties = PropertiesReader.readProperties(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "properties" + File.separator + "common" + File.separator + "email.properties");
        aut = commonProperties.getProperty("aut");
        autProperties = PropertiesReader.readProperties(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "properties" + File.separator + aut + File.separator + aut + ".properties");

        requestTimeOut = commonProperties.getProperty("request.timeout.in.seconds");
        isToSetJiraProxy = autProperties.getProperty("jira.set.proxy");
        emailTitle = emailProperties.getProperty("email.title");
        emailSignatureName = emailProperties.getProperty("email.signature.name");
        emailSignatureId = emailProperties.getProperty("email.signature.id");

        /* ToDo - update below block */
        if (null != System.getProperty("execution.app.name")) {
            appName = System.getProperty("execution.app.name");
            appInstalledLocation = CommonUtils.getApplicationPath(System.getProperty("execution.app.name"));
        } else {
            appName = autProperties.getProperty("execution.app.name");
            appInstalledLocation = CommonUtils.getApplicationPath(autProperties.getProperty("execution.app.name"));
        }

        windowsServerLocation = commonProperties.getProperty("winapp.server.location");
        if (null != System.getProperty("updateJira")) {
            System.out.println("reading jenkins set updateJira");
            isToUpdateJira = System.getProperty("updateJira");
        } else {
            isToUpdateJira = autProperties.getProperty("jira.update");
        }

        if (null != System.getProperty("jiraTestCycleName")) {
            System.out.println("reading jenkins set jira test cycle name");
            jiraTestCycleName = System.getProperty("jiraTestCycleName");
        } else {
            jiraTestCycleName = autProperties.getProperty("jira.cycle.name");
        }

        if (null != System.getProperty("test_filters")) {
            System.out.println("reading jenkins set test_filters");
            testFilter = System.getProperty("test_filters");
        } else {
            testFilter = autProperties.getProperty("tests.filters");
        }

        switch (autProperties.getProperty("execution.environment").toUpperCase()) {
            case "DEV":
                envConfigFile = "dev_config.properties";
                executionEnvironment = "Dev";
                break;
            case "PERF":
                envConfigFile = "perf_config.properties";
                executionEnvironment = "Perf";
                break;
            case "QA1":
                envConfigFile = "QA1_config.properties";
                executionEnvironment = "QA1";
                break;
            default:
                envConfigFile = "config.properties";
                executionEnvironment = "QA";
        }

    }

    public static String getTestFilter() {
        return testFilter;
    }

    public static String getRequestTimeOut() {
        return requestTimeOut;
    }

    public static String getAut() {
        return aut;
    }

    public static String getAppInstalledLocation() {
        return appInstalledLocation;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getWindowsServerLocation() {
        return windowsServerLocation;
    }

    public static boolean getIsToUpdateJira() {
        if (null == isToUpdateJira)
            return false;
        else
            return isToUpdateJira.equalsIgnoreCase("true");
    }


    public static boolean getIsToSetJiraProxy() {
        if (null == isToSetJiraProxy)
            return false;
        else
            return isToSetJiraProxy.equalsIgnoreCase("true");
    }

    public static void setAppInstalledLocation(String appInstalledLocation) {
        AutProperties.appInstalledLocation = appInstalledLocation;
    }

    public static String getExecutionEnvironmentConfigFileName() {
        return envConfigFile.trim();
    }

    public static String getExecutionEnvironment() {
        return executionEnvironment;
    }

    public static String getJiraTestCycleName() {
        return jiraTestCycleName;
    }


    public static String getEmailTitle() {
        return emailTitle;
    }

    public static String getEmailSignatureName() {
        return emailSignatureName;
    }

    public static String getEmailSignatureId() {
        return emailSignatureId;
    }

}
