package com.unicorn.utils;

import com.unicorn.base.logger.Logger;
import org.testng.Assert;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class PropertiesReader {

    public static Properties readProperties(String filePath) {
        Properties properties = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Logger.assertFail("Can't file at: " + file.getAbsolutePath());
            }
            FileReader reader = new FileReader(filePath);
            properties = new Properties();
            properties.load(reader);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to read properties file: " + filePath);
        }
        return properties;
    }
}
