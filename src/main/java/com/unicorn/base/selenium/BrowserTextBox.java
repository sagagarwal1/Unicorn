package com.unicorn.base.selenium;

import com.unicorn.base.BaseTestSetup;
import com.unicorn.base.logger.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

public class BrowserTextBox extends BaseTestSetup {

    private WebDriver driver;

    public BrowserTextBox() {
        this.driver = webDriver;
    }

    /**
     * Clears text box value and sends text in text box
     *
     * @param element Web elements
     * @param value   text to enter
     */
    public void sendKeys(WebElement element, String value) {
        try {
            element.clear();
            // elements.sendKeys(Keys.TAB);
            element.sendKeys(value);
        } catch (NoSuchElementException exception) {
            Logger.error("Failed on enter " + value + " in a checkbox.." + exception.getMessage(), exception);
            Assert.fail("Text Box: " + element + "not found.");
        } catch (Exception e) {
            Logger.error("Failed to enter " + value + " in a checkbox.." + e.getMessage(), e);
            Assert.fail("Failed to enter '" + value + "' in Text Box: '" + element + "'.", e);
        }
    }


    /**
     * Sends text in text box
     *
     * @param element        WebElement
     * @param clearBeforeSet is set false if not to clear
     * @param value          to enter
     */
    public void sendKeys(WebElement element, boolean clearBeforeSet, String value) {
        try {
            if (clearBeforeSet) {
                element.clear();
            }
            element.sendKeys(value);
        } catch (NoSuchElementException exception) {
            Logger.error("Failed to enter " + value + " in a text.." + exception.getMessage(), exception);
            Assert.fail("Text Box: " + element + "not found.");
        } catch (Exception e) {
            Logger.error("Failed to enter " + value + " in a text.." + e.getMessage(), e);
            Assert.fail("Failed to enter '" + value + "' in Text Box: '" + element + "'.", e);
        }
    }

    /**
     * Sends date in text box
     *
     * @param element        WebElement
     * @param clearBeforeSet is set false if not to clear
     * @param date           value in date format
     */
    public void sendKeysDate(WebElement element, boolean clearBeforeSet, Date date) {
        try {
            if (clearBeforeSet) {
                element.clear();
            }
            SimpleDateFormat format = new SimpleDateFormat();
            element.sendKeys(format.format(date));
        } catch (NoSuchElementException exception) {
            Logger.error("Failed to enter " + date + " in a checkbox.." + exception.getMessage(), exception);
            Assert.fail("Text Box: " + element + "not found.");
        } catch (Exception e) {
            Logger.error("Failed to enter " + date + " in a checkbox.." + e.getMessage(), e);
            Assert.fail("Failed to enter '" + date + "' in Text Box: '" + element + "'.", e);
        }
    }


    /**
     * This method is useful when you want to set value in Attribute value,
     * without loosing focus from text field after cleaning it.
     *
     * @param element WebElement
     * @param text    Text to be enter in text box.
     */
    public void setTextByAttrValue(WebElement element, String text) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].focus();arguments[0].setAttribute('value',arguments[1])", element, text);
        } catch (Exception e) {
            Logger.error("Failed to enter " + text + " in a checkbox.." + e.getMessage(), e);
            Assert.fail("Failed to enter '" + text + "' in Text Box: '" + element + "'.", e);
        }
    }

    /**
     * Scrolls the specified text area to the bottom.
     *
     * @param textAreaId The id of the text area to scroll
     */
    public void scrollTextAreaToBottom(String textAreaId) {
        WebElement textArea = driver.findElement(By.id(textAreaId));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", textArea);
        } catch (Exception e) {
            Assert.fail("Exception while scrolling text area using javascript", e);
        }
    }
}
