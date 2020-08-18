package com.unicorn;

import com.unicorn.utils.CouchBaseUtility;
import com.unicorn.base.BaseTestSetup;
import com.unicorn.base.jiraintegration.JiraServices;
import com.unicorn.base.logger.Logger;
import com.unicorn.base.selenium.BrowserWindows;
import com.unicorn.report.AllureReportManager;
import com.unicorn.report.EmailableReportManager;
import com.unicorn.stepdefinitions.CucumberBase;
import com.unicorn.utils.AutProperties;
import com.unicorn.utils.CommonUtils;
import cucumber.api.CucumberOptions;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.OutputType;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * @author Vivek Lande
 */
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"com.unicorn.stepdefinitions"},
        monochrome = true,
        dryRun = false,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-pretty",
                "json:target/cucumber-reports/CucumberTestReport.json",
                "rerun:target/cucumber-reports/rerun.txt",
                "io.qameta.allure.cucumber4jvm.AllureCucumber4Jvm"
        })

@Test
public class TestRunner extends Logger {
    private JiraServices jiraServices;
    private boolean isWinappServerStarted = false;

    public TestRunner() {
        AutProperties.initAutProperties();
        jiraServices = new JiraServices(AutProperties.getAut());
    }

    /**
     * Capture screenshot
     *
     * @param screenshotName screenshot name
     * @return String screenshot path
     */
    @Nullable
    public static String captureScreenshot(String screenshotName) {
        if (BaseTestSetup.windowsDriver == null) {
            return null;
        }

        String directory_name = System.getProperty("user.dir") + File.separator + "target" + File.separator + "screenshots" + File.separator + CucumberBase.scenarioName + File.separator;
        String screenshotPath = null;
        try {
            File scrFile = BaseTestSetup.windowsDriver.getScreenshotAs(OutputType.FILE);
            try {
                if (!new File(directory_name).exists()) {
                    new File(directory_name).mkdir();
                }
            } catch (Exception e) {
                Logger.error("Failed to create directory -" + directory_name + ". " + e);
                throw new IOException("Failed to create directory -" + directory_name + ". " + e);
            }
            try {
                screenshotPath = directory_name + screenshotName + "_" + CommonUtils.getCurrentTimeStamp() + ".png";
                FileUtils.copyFile(scrFile, new File(screenshotPath));
                return new File(screenshotPath).getAbsolutePath();
            } catch (Exception e) {
                Logger.error("Failed to copy file screenshot file!" + screenshotName, e);
            }
        } catch (Exception e) {
            Logger.error("Debug: screenshot: failed: End " + e);
        }
        return "";
    }


    @BeforeSuite(alwaysRun = true)
    public void setupWindowsDriverServer() {
        setupWinappServer();
        System.setProperty("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        Logger.info("Execution start time: " + System.getProperty("startTime"));
        System.setProperty("cucumber.options", "--tags " + AutProperties.getTestFilter());
        System.setProperty("allure.results.directory", System.getProperty("user.dir") + File.separator + "target\\allure-results");
        System.setProperty("allure.reports.directory", System.getProperty("user.dir") + File.separator + "target\\allure-reports");
    }

    @AfterSuite(alwaysRun = true)
    public void stopWindowsServer() {
        stopWinappServer();
        System.setProperty("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        Logger.info("Execution end time: " + System.getProperty("endTime"));
        ArrayList<String> cucumberJson = new ArrayList<>();
        cucumberJson.add(System.getProperty("user.dir") + File.separator + "target" + File.separator + "cucumber-reports" + File.separator + "CucumberTestReport.json");
        AllureReportManager.generateAllureReport();
        new EmailableReportManager().generateReport("Emailable.html");
    }

    public void setupWinappServer() {
        BaseTestSetup.stopWinappDriverServer();
        BaseTestSetup.setupWinappDriverServer();
        isWinappServerStarted = true;
    }

    public void stopWinappServer() {
        if (isWinappServerStarted) {
            BaseTestSetup.stopWinappDriverServer();
            isWinappServerStarted = false;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult iTestResult) {
        BrowserWindows.allSessions.clear();
        String executionStatus = getStatusId(iTestResult);

        if (AutProperties.getIsToUpdateJira()) {
            Logger.info("Updating status for jira tickets: " + CucumberBase.jira);
            String jiraTickets[] = CucumberBase.jira.split(",");
            for (String jiraTicket : jiraTickets) {
                jiraServices.updateTicketExecutionStatus(jiraServices.getExecutionId(jiraTicket), executionStatus);
            }
        }

        CouchBaseUtility.closeBucket();
        Logger.info("---------------------------------------------------------------------------------");
    }

    /**
     * This will return the execution status id base on the name of status
     *
     * @param status status name as SUCCESS, FAIL, WIP, BLOCKED, NA, UNEXECUTED.
     * @return id
     */
    private String getStatusId(String status) {
        switch (status) {
            case "SUCCESS":
                return "1";
            case "FAILURE":
                return "2";
            case "WIP":
                return "3";
            case "BLOCKED":
                return "4";
            case "NA":
                return "5";
            case "UNEXECUTED":
                return "-1";
        }
        return null;
    }

    /**
     * Get test execution status id
     */
    private String getStatusId(ITestResult iTestResult) {
        switch (iTestResult.getStatus()) {
            case ITestResult.SUCCESS:
                return "1";
            case ITestResult.FAILURE:
                return "2";
            default:
                return "-1";
        }
    }
}

