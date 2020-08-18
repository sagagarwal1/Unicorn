package com.unicorn.report;

import com.unicorn.base.BaseTestSetup;
import com.unicorn.base.logger.Logger;
import com.unicorn.utils.AutProperties;
import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.*;
import java.util.Properties;

public class AllureReportManager {

    /**
     * This will generate allure report by calling gradle task
     */
    public static void generateAllureReport() {
        Logger.info("Generating allure report");
        try {
            //generateEnvironmentDetails();
            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd " + System.getProperty("user.dir") + " && gradle allureReport");
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                Logger.info(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Failed to generate allure report", e);
        }
    }

    /**
     * This will attach screenshot to allure report
     */
    public static void addScreenshotToAllureReport(String screenShotName) {
        try {
            File scrFile;
            if (null != BaseTestSetup.windowsDriver) {
                scrFile = ((TakesScreenshot) BaseTestSetup.windowsDriver).getScreenshotAs(OutputType.FILE);
                Allure.addAttachment(screenShotName, FileUtils.openInputStream(scrFile));
            } else if (null != BaseTestSetup.webDriver) {
                scrFile = ((TakesScreenshot) BaseTestSetup.webDriver).getScreenshotAs(OutputType.FILE);
                Allure.addAttachment(screenShotName, FileUtils.openInputStream(scrFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.warning("Unable to add Screenshot for " + screenShotName, e);
        }
    }

    /**
     * Add attachment to the allure report, like query etc.
     *
     * @param attachmentName
     * @param attachment
     */
    public static void addAttachment(String attachmentName, String attachment) {
        Allure.addAttachment(attachmentName, attachment);
    }

    private static void generateEnvironmentDetails() throws Exception {
        String allureResultPath = System.getProperty("user.dir") + "\\target\\allure-results\\environment.properties";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(AutProperties.getAppInstalledLocation() + ".manifest")));

        String line;
        while (true) {
            line = bufferedReader.readLine();
            if (line == null)
                break;
            else if (line.contains("assemblyIdentity") && line.contains("version") && line.contains("ACLAIM")) {
                break;
            }
        }
        String[] info = line.split("\\s");
        String name = (info[3].replaceAll("\"", "").split("=")[1]).split("\\.")[0];
        String version = info[4].replaceAll("\"", "").split("=")[1];

        try (OutputStream outputStream = new FileOutputStream(allureResultPath)) {
            Properties properties = new Properties();
            properties.setProperty("Execution.Environment", AutProperties.getExecutionEnvironment());
            properties.setProperty("Build.Number", version);
            properties.setProperty("Build.Name", name);
            properties.setProperty("Operating.System", System.getProperty("os.name"));
            properties.setProperty("Owner", "GP Compliance");
            // save properties to project root folder
            properties.store(outputStream, null);

        } catch (Exception e) {
            Logger.error("Exception while generating Environment details", e);
        }
    }
}
