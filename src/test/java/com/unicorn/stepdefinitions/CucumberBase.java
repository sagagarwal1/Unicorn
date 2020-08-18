package com.unicorn.stepdefinitions;

import com.unicorn.base.BaseTestSetup;
import com.unicorn.base.jiraintegration.JiraServices;
import com.unicorn.base.logger.Logger;
import com.unicorn.base.recorder.ScreenRecordingManager;
import com.unicorn.report.AllureReportManager;
import com.unicorn.utils.AutProperties;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

import java.awt.*;
import java.io.IOException;
import java.util.Collection;

public class CucumberBase {
    public static String jira;
    public static String scenarioName;
    public ScreenRecordingManager screenRecorder = new ScreenRecordingManager();
    private boolean isAppStarted = false;

    @Before("@UI")
    public void cucumberBefore() throws IOException, AWTException {

        Logger.info("-------------------------Starting " + AutProperties.getAppName() + " Session---------------------------------");
        if (null != AutProperties.getAppName()) {
            BaseTestSetup.setupWindowsApplication(AutProperties.getAppName());
        }else if(null != AutProperties.autProperties.getProperty("browser.name")){
            BaseTestSetup.setupBrowser();
        }
        isAppStarted = true;
        screenRecorder.startRecording();
    }

    @After("@UI")
    public void cucumberAfter(Scenario scenario) {
        System.out.println("Inside cucumber after");
        screenRecorder.isTestFailed = scenario.isFailed();
        if (scenario.isFailed()) {
            Logger.debug("Scenario Failed: adding screenshot to report");
            AllureReportManager.addScreenshotToAllureReport(scenario.getName());
        }
        screenRecorder.stopRecording();
        if (isAppStarted) {
            if (null != AutProperties.getAppName()) {
                BaseTestSetup.shutDownApplication(AutProperties.getAppName());
            }else if(null != AutProperties.autProperties.getProperty("default.browser")){
                BaseTestSetup.closeBrowserSession();
            }

            isAppStarted = false;
        }
    }

    @Before(order = 1)
    public void setup(Scenario scenario) {
        Logger.info("====================== Executing: " + scenario.getName() + " ================================");
        scenarioName = (scenarioName = "") + scenario.getName().replaceAll("\"", "");
        screenRecorder.testName = (screenRecorder.testName = "") + scenarioName;

        if (AutProperties.getIsToUpdateJira()) {
            Collection<String> collection = scenario.getSourceTagNames();
            String[] tags = collection.toString().replaceAll("\\[|\\]", "").split("@");
            jira = "";
            for (String tag : tags) {
                if (tag.toLowerCase().contains("jira::")) {
                    jira = (jira + tag.split("::")[1].trim()).trim();
                }
            }
            Logger.info("Jira's:" + jira + " status will be updated on execution of current Scenario: "
                    + scenarioName);
            Logger.info("Adding Jira's:" + jira + "to the execution cycle");
            JiraServices jiraServices = new JiraServices(AutProperties.getAut());
            String[] jiraTickets = jira.split(",");
            for (String jiraTicket : jiraTickets) {
                jiraServices.addTestToCycle(jiraTicket);
            }
        }
    }

    @After(order = 1)
    public void testDataCleanup(Scenario scenario) {
        //TODO: Add clean up steps
        Logger.info("====================== End: " + scenario.getName() + " ================================");
    }
}
