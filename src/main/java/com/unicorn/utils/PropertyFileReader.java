package com.unicorn.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public class PropertyFileReader {

  final static Logger logger = Logger.getLogger(PropertyFileReader.class);
  Properties prop = new Properties();
  InputStream input = null;

  public String getPropertyValue(String propertyFile, String propertyName) {
    Properties prop = new Properties();
    InputStream input = null;
    FileReader fr = null;
    BufferedReader br = null;
    String propValue = null;
    try {
      logger.info(propertyFile);
      logger.info("absolute path " + new File(".").getAbsolutePath());
      input = new FileInputStream(propertyFile);
      prop.load(input);
      propValue = prop.getProperty(propertyName);
      System.out.println(propValue);
    } catch (IOException ex) {
      ex.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return propValue;

  }
}
