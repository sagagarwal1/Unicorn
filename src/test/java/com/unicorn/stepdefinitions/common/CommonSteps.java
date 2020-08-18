package com.unicorn.stepdefinitions.common;

import com.unicorn.base.BaseTestSetup;
import com.unicorn.base.logger.Logger;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;


public class CommonSteps {

    @Given("launch {string} application")
    public void launchApp(String appName) {
        BaseTestSetup.setupWindowsApplication(appName);
        Logger.info("Launched application: " + appName);
    }

    @And("close application {string}")
    public void closeApplication(String appName) {
        BaseTestSetup.shutDownApplication(appName);
        Logger.info("Close application - " + appName);
    }

    @And("launch {string} on {string}")
    public void launchBrowserApplication(String url, String browserName) {
        BaseTestSetup.setupBrowser(browserName,url);
        Logger.info("Launch application \"%s\" on \"%s\" browser", url,browserName);
    }
}
