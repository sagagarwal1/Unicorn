package com.unicorn.base;

import com.unicorn.base.logger.Logger;
import com.unicorn.utils.AutProperties;
import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import java.io.IOException;

/**
 * @author Vivek Lande
 */
public class BaseTestSetup {
    public static WindowsDriver windowsDriver;
    public static WebDriver webDriver;

    public static void setupWinappDriverServer() {
        Logger.info("-----------------------Setting up Winapp Server-----------------");
        DriverManager.startWinappServer();
    }

    public static void stopWinappDriverServer() {
        DriverManager.stopWinappServer();
        Logger.info("----------------------Shut down winapp Server--------------------");

    }

    public static void setupWindowsApplication(String AppName) {
        try {
            Process killApp = Runtime.getRuntime().exec("taskkill /f /t /fi \"pid gt 0\" /im \"" + "win32calc" + "*\"");
            killApp.waitFor();
            Thread.sleep(3000);
            windowsDriver = DriverManager.getWindowsDriver();
            DriverManager.takeControlToScreenLockFunctioning();

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to setup windows driver", e);
        }
    }

    public static void setupBrowser() {
            webDriver = DriverManager.initDriver(AutProperties.autProperties.getProperty("default.browser"));
    }

    public static void setupBrowser(String browserName, String URL) {
            webDriver = DriverManager.initDriver(browserName);
            webDriver.get(URL);
    }

    public static void closeBrowserSession() {
        try{
            if (webDriver != null) {
                webDriver.quit();
                webDriver = null;
                killTask("geckodriver*.exe");
                killTask("chromedriver*.exe");
                killTask("IEDriverServer.exe");
                killTask("Microsoft*WebDriver*.exe");

            }
        }catch(Exception e){
            e.printStackTrace();
            Logger.assertFail("Failed to close browser");
        }
    }

    public static void killTask(String taskName){
        try {
            Process killTask = Runtime.getRuntime().exec("taskkill /f /t /fi \"pid gt 0\" /im \""+taskName+"\"");
            killTask.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            Logger.assertFail("Failed to close ");
        }
    }
    public static void shutDownApplication(String AppName) {
        Logger.info("Closing " + AppName + " Session");
        try {
            if (windowsDriver != null) {
                Process shutDownApp = Runtime.getRuntime().exec("taskkill /f /t /fi \"pid gt 0\" /im \"" + AppName + "*\"");   // + path[path.length - 1]);
                shutDownApp.waitFor();
                Logger.info(AppName + " closed successfully");
                windowsDriver = null;
                DriverManager.releaseControlToScreenLockFunctioning();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.assertFail("Failed to close " + AppName);
        }
    }
}
