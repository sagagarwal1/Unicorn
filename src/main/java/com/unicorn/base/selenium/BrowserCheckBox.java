package com.unicorn.base.selenium;

import com.unicorn.base.logger.Logger;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.NoSuchElementException;

public class BrowserCheckBox {
    /**
     * Selects or checks checkbox button if not already checked
     *
     * @param element: elements to be checked
     */
    public void selectCheckboxButton(WebElement element) {
        try {
            if (isCheckboxDisplayed(element) && isCheckboxEnabled(element)) {
                if (!isCheckboxSelected(element)) {
                    element.click();
                }
            }
        } catch (NoSuchElementException exception) {
            Logger.error("Checkbox Button: " + element + " not found.", exception);
            Assert.fail("Checkbox Button: " + element + " not found.", exception);
        }
    }


    /**
     * deSelects or unchecks checkbox button if not already unchecked
     *
     * @param element : elements to be unchecked
     */
    public void deSelectCheckboxButton(WebElement element) {

        try {
            if (isCheckboxDisplayed(element) && isCheckboxEnabled(element)) {
                if (isCheckboxSelected(element)) {
                    element.click();
                }
            }
        } catch (NoSuchElementException exception) {
            Logger.error("Checkbox Button: " + element + " not found." + exception);
          Assert.fail("Checkbox Button: " + element + " not found.", exception);
        }
    }

    /**
     * returns true if checkbox is dislayed, false otherwise
     *
     * @param element : elements to be verified
     */
    public boolean isCheckboxDisplayed(WebElement element) {

        boolean status = false;
        try {
            status = element.isDisplayed();
        } catch (NoSuchElementException exception) {
            Logger.error("Checkbox Button: " + element + " not found.", exception);
            Assert.fail("Checkbox Button: " + element + " not found.", exception);
        }
        return status;
    }


    /**
     * returns true if checkbox is enabled, false otherwise
     *
     * @param element : elements to be verified
     */
    public boolean isCheckboxEnabled(WebElement element) {

        boolean status = false;
        try {
            status = isCheckboxDisplayed(element) && element.isEnabled();
        } catch (NoSuchElementException exception) {
            Logger.error("Checkbox Button: " + element + " not found.", exception);
            Assert.fail("Checkbox Button: " + element + " not found.", exception);
        }
        return status;
    }


    /**
     * returns true if checkbox is selected, false otherwise
     *
     * @param element : elements to be verified
     */
    public boolean isCheckboxSelected(WebElement element) {

        boolean status = false;
        try {
            status = isCheckboxDisplayed(element) && element.isSelected();
        } catch (NoSuchElementException exception) {
            Logger.error("Checkbox Button: " + element + " not found.", exception);
            Assert.fail("Checkbox Button: " + element + " not found.", exception);
        }
        return status;
    }

    /**
     * Toggles checkbox value to check and uncheck
     *
     * @param element : elements to be checked or unchecked
     * @param check   : true if checkbox needs to be selected, false otherwise
     */
    public void toggleCheckbox(WebElement element, boolean check) {
        try {
            if (isCheckboxDisplayed(element) && isCheckboxEnabled(element)) {
                if (check) {
                    if (!isCheckboxSelected(element)) {
                        element.click();
                    }
                } else {
                    if (isCheckboxSelected(element)) {
                        element.click();
                        Logger.info("Checkbox Button : " + element + " clicked.");
                    }
                }
            }
        } catch (NoSuchElementException exception) {
            Logger.error("Checkbox Button: " + element + " not found.", exception);
            Assert.fail("Checkbox Button: " + element + " not found.", exception);
        }
    }
}
