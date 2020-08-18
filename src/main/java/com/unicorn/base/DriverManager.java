package com.unicorn.base;

import com.unicorn.base.logger.Logger;
import com.unicorn.utils.AutProperties;
import com.unicorn.utils.CommonUtils;
import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.Point;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class DriverManager {
    private static Thread winappThread;
    private static Thread screenWakedUpThread;
    private static PrintWriter printWriter;

    /**
     * Setup windows driver
     *
     * @return WindowsDriver
     */
    public static WindowsDriver getWindowsDriver() {
        WindowsDriver windowsDriver = null;
        String[] path = AutProperties.getAppInstalledLocation().split("\\\\");
        Process taskKill;
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            String appInstalledPath = AutProperties.getAppInstalledLocation();
            if (!new File(appInstalledPath).exists()) {
                Logger.assertFail("Can't find Application installed at: " + appInstalledPath);
            }
            capabilities.setCapability("app", appInstalledPath);
            capabilities.setCapability("platormName", "Windows");
            capabilities.setCapability("deviceName", "WindowsPC");

            try {
                windowsDriver = new WindowsDriver(new URL("http://127.0.0.1:4723"), capabilities);
            } catch (SessionNotCreatedException snce) {
                Logger.error(
                        "Exception while creating windows driver instance!..ignoring once and retrying");
                taskKill = Runtime.getRuntime().exec("taskkill /f /fi \"pid gt 0\" /im " + path[path.length - 1]);
                taskKill.waitFor();
                try {
                    windowsDriver = new WindowsDriver(new URL("http://127.0.0.1:4723"), capabilities);
                } catch (WebDriverException wde) {
                    if (wde.getMessage()
                            .contains("Failed to locate opened application window with appId:")) {
                        Logger.error(
                                "Exception while creating windows driver instance!..Can't locate opened application, giving the another try");
                        taskKill = Runtime.getRuntime()
                                .exec("taskkill /f /fi \"pid gt 0\" /im " + path[path.length - 1]);
                        taskKill.waitFor();
                        windowsDriver = new WindowsDriver(new URL("http://127.0.0.1:4723"), capabilities);
                    } else {
                        wde.printStackTrace();
                        Logger.assertFail("Exception while creating windows driver instance");
                    }
                }
            }
            int timeout = 180;
            while (timeout > 0) {
                try {
                    Thread.sleep(1000);
//                    windowsDriver.findElementByName("Calculator");
                    break;
                } catch (NoSuchElementException e) {
                    timeout--;
                }
            }
            Assert.assertNotNull(windowsDriver);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.assertFail("Exception while setting up windows driver", e);
        }
        return windowsDriver;
    }

    /**
     * Start winapp server
     */
    public static void startWinappServer() {
        winappThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(AutProperties.getWindowsServerLocation());
                    if (!file.exists()) {
                        Assert.fail("Can't find winapp server installed at: " + file.getAbsolutePath());
                    }
                    List<String> command = new ArrayList<String>();
                    command.add(AutProperties.getWindowsServerLocation());

                    ProcessBuilder builder = new ProcessBuilder(command);
                    //builder.inheritIO();
                    final Process process = builder.start();
                    InputStream is = process.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(is);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream("target" + File.separator + "logs" + File.separator + "winAppServer" + ".log"), "UTF-8"));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        printWriter.println(line);
                    }
                    Logger.info("Winapp Server started successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail("Failed to start Winapp driver server !", e);
                }
            }
        });
        winappThread.setDaemon(true);
        winappThread.start();
    }

    /**
     * Stop winaap server
     */
    public static void stopWinappServer() {
        try {
            if (winappThread != null) {
                printWriter.close();
                winappThread.interrupt();
                winappThread.stop();
                Logger.info("Windows driver server stopped successfully......!");
            }
        } catch (Exception e) {
            Logger.error("Failed to stop winapp server", e);
        }
    }

    public static void takeControlToScreenLockFunctioning() {
        screenWakedUpThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Logger.info("Taking control to screen lock functioning!");
                    Robot robot = new Robot();
                    Random random = new Random();
                    int time = 0;
                    while (true) {
                        int x = random.nextInt() % 640;
                        int y = random.nextInt() % 480;
                        Logger.info("Keeping screen lock control, time: " + time + " minutes");
                        Thread.sleep(2000 * 60);
                        time += 2;
                        robot.mouseMove(x, y);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.assertFail("Failed to take control on screen lock functioning!", e);
                }
            }
        });
        screenWakedUpThread.setDaemon(true);
        screenWakedUpThread.start();
    }

    public static void releaseControlToScreenLockFunctioning() {
        try {
            if (screenWakedUpThread != null) {
                screenWakedUpThread.interrupt();
                screenWakedUpThread.stop();
                Logger.info("Release control over screen lock functioning");
            }
        } catch (Exception e) {
            Logger.error("Failed to release control over screen lock functioning", e);
        }
    }

    /**
     * Initialize Web Driver
     */
    public static WebDriver initDriver(String browserName) {
        WebDriver driver = null;
        try {
            switch (browserName.trim().toUpperCase()) {
                case "FIREFOX":
                case "FF":
                case "MOZILLA FIREFOX": {
                    System.setProperty("webdriver.gecko.driver", CommonUtils.getResourcePath("drivers") + File.separator + "geckodriver.exe");
                    FirefoxOptions options = new FirefoxOptions();
                    options.addPreference("browser.download.folderList", 2);
                    options.addPreference("browser.download.manager.showWhenStarting", false);
                    options.addPreference("browser.download.dir", System.getProperty("user.dir") + File.separator + "Download");
                    options.addPreference("browser.download.downloadDir", System.getProperty("user.dir") + File.separator + "Download");
                    options.addPreference("browser.download.defaultFolder", System.getProperty("user.dir") + File.separator + "Download");
                    options.addPreference("browser.download.manager.closeWhenDone", true);
                    options.addPreference("pdfjs.disabled", true);
                    options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/zip,text/csv,application/msword,application/excel,application/pdf," +
                            "application/vnd.ms-excel,application/msword,application/unknown,application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    driver = new FirefoxDriver(options);
                    Logger.info(browserName + " driver is initialized..");
                }
                break;
                case "INTERNET EXPLORER":
                case "INTERNETEXPLORER":
                case "IE": {
                    System.setProperty("webdriver.ie.driver", CommonUtils.getResourcePath("drivers") + File.separator + "IEDriverServer.exe");
                    driver = new InternetExplorerDriver();
                    Logger.info(browserName + " driver is initialized..");
                }
                break;
                case "EDGE":
                case "MICROSOFT EDGE": {
                    System.setProperty("webdriver.edge.driver", CommonUtils.getResourcePath("drivers") + File.separator + "msedgedriver.exe");
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.setCapability("edge_binary",CommonUtils.getApplicationPath("MicrosoftEdge"));
                    driver = new EdgeDriver();
                    Logger.info(browserName + " driver is initialized..");
                }
                break;
                case "CHROME":
                case "GOOGLE CHROME": {
                    System.setProperty("webdriver.chrome.driver", CommonUtils.getResourcePath("drivers") + File.separator + "chromedriver.exe");
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("test-type");
//                    chromeOptions.addArguments("--headless", "window-size=1280,1024", "--no-sandbox"); // Enable for headless option
                    driver = new ChromeDriver(chromeOptions);
                    Logger.info(browserName + " driver is initialized..");
                }
                break;
                default:
                    Assert.fail("Invalid Browser Type!");
            }//switch

            String waitTime = AutProperties.getRequestTimeOut();
            driver.manage().timeouts().implicitlyWait(Long.parseLong(waitTime), TimeUnit.SECONDS);
            driver.manage().window().setPosition(new Point(0, 0));
            driver.manage().window().maximize();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception while setting up WebDriver", e);
        }
        return driver;
    }
}

