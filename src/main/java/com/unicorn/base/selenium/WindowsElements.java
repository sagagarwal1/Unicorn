package com.unicorn.base.selenium;


import com.unicorn.base.BaseTestSetup;
import com.unicorn.base.logger.Logger;
import com.unicorn.utils.AutProperties;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.touch.TouchActions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;


public class WindowsElements extends BaseTestSetup {
    /**
     * Verifies the Actual Text as compared to the Expected Text
     *
     * @param actualText Text from web page
     * @param expText    Expected text
     * @return boolean true if actual and expected are same else return false
     */
    public boolean verifyText(String actualText, String expText) {
        try {
            expText = expText.trim();
            actualText = actualText.trim();
            if (expText.isEmpty() && actualText.isEmpty()) {
                return true;
            }
            return expText.equalsIgnoreCase(actualText);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Verifies the Actual Text to the Expected Text and compare case as well
     *
     * @param actualText Text from web page
     * @param expText    Expected text
     * @return boolean true if actual and expected are same else return false
     */
    public boolean verifyTextCaseSensitive(String actualText, String expText) {
        try {
            expText = expText.trim();
            actualText = actualText.trim();
            if (expText.isEmpty() && actualText.isEmpty()) {
                return true;
            }
            return expText.equals(actualText);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Check if elements displayed on page
     *
     * @param webElement
     * @return boolean value
     */
    public boolean isElementDisplayed(WebElement webElement) {
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                return webElement.isDisplayed();
            } catch (StaleElementReferenceException sere) {
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(20000);
                        Logger.error("stale elements exception while waiting for elements:" + webElement + " to be display: ", sere);
                        Logger.info("handling stale elements reference: retry count-" + retryCount);
                        continue;
                    } catch (InterruptedException e) {
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return false;
    }

    /**
     * Returns a boolean indicating whether the specified elements is enabled.
     *
     * @param element
     * @return A boolean, true if the specified elements exists and is enabled, false otherwise.
     */
    public static boolean isElementEnabled(WebElement element) {
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                return element.isEnabled();
            } catch (StaleElementReferenceException sere) {
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(20000);
                        Logger.error("stale elements exception while waiting for elements:" + element + " to be enabled: ", sere);
                        Logger.info("handling stale elements reference: retry count-" + retryCount);
                        continue;
                    } catch (InterruptedException e) {
                    }
                }
            } catch (Exception e) {
                throw e;
            }
        }
        return false;
    }


    /**
     * Will wait until a specified  elements isDisplayed method is true. If a dialog is display it will fail.
     *
     * @param element
     * @param waitForNotDisplayed, Will wait for an elements to not be displayed
     */
    public void waitForWebElementIsDisplayed(final WebElement element, final boolean waitForNotDisplayed) throws
            Exception {
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())))
                        .until(new ExpectedCondition<Boolean>() {
                            public Boolean apply(WebDriver d) {

                                if (waitForNotDisplayed) {
                                    return !element.isDisplayed();
                                } else {
                                    return element.isDisplayed();
                                }
                            }
                        });
                break;
            } catch (StaleElementReferenceException sere) {
                Logger.error("stale elements exception while waiting for elements:" + element + " to be display: ", sere);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(20000);
                        Logger.info("handling stale elements reference: retry count-" + retryCount);
                        continue;
                    } catch (InterruptedException ite) {
                        throw new Exception("Exception while waiting for elements to be displayed", ite);
                    }
                }
            } catch (TimeoutException te) {
                Logger.error(element + " is not present! its not either attached to the DOM, getting timeout", te);
                break;
            } catch (Exception e) {
                Logger.error(element + " is not present! its not either attached to the DOM, getting timeout", e);
                break;
            }
        }
    }

    public void waitForWebElementIsDisplayed(final WebElement element, int timeOutInSec,
                                             final boolean waitForNotDisplayed) throws Exception {
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                (new WebDriverWait(windowsDriver, timeOutInSec))
                        .until(new ExpectedCondition<Boolean>() {
                            public Boolean apply(WebDriver d) {

                                if (waitForNotDisplayed) {
                                    return !element.isDisplayed();
                                } else {
                                    return element.isDisplayed();
                                }
                            }
                        });
                break;
            } catch (StaleElementReferenceException sere) {
                Logger.error("stale elements exception while waiting for elements:" + element + " to be display: ", sere);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(20000);
                        Logger.info("handling stale elements reference: retry count-" + retryCount);
                        continue;
                    } catch (InterruptedException ite) {
                        throw new Exception("Exception while waiting for elements to be displayed", ite);
                    }
                }
            } catch (TimeoutException te) {
                Logger.error(element + " is not present! its not either attached to the DOM, getting timeout", te);
                break;
            } catch (Exception e) {
                Logger.error(element + " is not present! its not either attached to the DOM, getting timeout", e);
                break;
            }
        }
    }

    public void waitForWebElementIsDisplayed(final WebElement element, int timeOutInSec) throws Exception {
        waitForWebElementIsDisplayed(element, timeOutInSec, false);
    }

    /**
     * Will wait until a specified  elements isDisplayed method is true. If a dialog is display it will fail.
     *
     * @param element
     */
    public void waitForWebElementIsDisplayed(final WebElement element) throws Exception {
        waitForWebElementIsDisplayed(element, false);
    }


    /**
     * Clicks the specified WebElement. This includes focus-handling logic to work properly where the elements is not
     * displayed, or elements in focus is overlapped by another elements (ex: floating banner, menu bar, etc)
     *
     * @param element WebElement to be clicked.
     */
    public void clickElementAfterFocus(WebElement element) {
        clickElementAfterFocus(element, false);
    }

    /**
     * Clicks the specified WebElement. This includes focus-handling logic to work properly where the elements is not
     * displayed, or elements in focus is overlapped by another elements (ex: floating banner, menu bar, etc)
     *
     * @param element        WebElement to be clicked.
     * @param scrollToBottom - If true, scroll to bottom of the page after shifting focus
     */
    public void clickElementAfterFocus(WebElement element, boolean scrollToBottom) {
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                waitForWebElementIsEnabled(element, false);
                waitForElementTobeClickable(element);
                if (scrollToBottom) {
                    TouchActions touchActions = new TouchActions(BaseTestSetup.windowsDriver);
                    touchActions.scroll(100, 0);
                }
                try {
                    element.click();
                    break;
                } catch (WebDriverException wde1) {
                    Logger.error("exception while clicking selenium elements!", wde1);
                    try {
                        TouchActions touchActions = new TouchActions(BaseTestSetup.windowsDriver);
                        touchActions.scroll(100, 0);
                        element.click();
                        break;
                    } catch (WebDriverException wde2) {
                        element.click();
                        break;
                    }
                }
            } catch (StaleElementReferenceException e) {
                Logger.error("Stale elements exception while waiting clicking on elements", e);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(1000);
                        continue;
                    } catch (Exception e1) {
                    }
                    Logger.info("handling stale elements reference: retry count-" + retryCount);
                }

                Assert.fail("StaleElementException while clicking selenium elements", e);
            } catch (Exception ex) {
                ex.printStackTrace();
                Assert.fail("exception while clicking selenium elements!", ex);
            }
        }
    }

    /**
     * Selects an option from a drop down control based on option value
     *
     * @param dropDownId: the id for the combo box
     * @param value:      The value of the option
     * @throws Exception
     */
    public void chooseSelectValue(final WebElement dropDownId, final String value) throws Exception {
        new Select(dropDownId).selectByValue(value);
    }


    /**
     * Clicks on a button by looking at all button objects on the page, and choosing the one that contains the supplied
     * text string.
     *
     * @param buttonText some text from the button
     */
    public void clickButtonByText(String buttonText) throws Exception {
        // Should add checks here to make sure only a single button is selected.
        WebElement buttonXPath = windowsDriver.findElement(By.xpath("//button[contains(.,'" + buttonText + "')]"));
        waitForWebElementIsDisplayed(buttonXPath, false);
        waitForWebElementIsEnabled(buttonXPath, false);
        clickElementAfterFocus(buttonXPath);
    }

    /**
     * This method is used to check if elements has selected or not
     *
     * @param element
     * @return
     */
    public boolean isInputChecked(WebElement element) {
        String checkedValue = element.getAttribute("checked");
        return checkedValue != null && checkedValue.equalsIgnoreCase("true");

    }


    /**
     * Will wait until a specified By elements's isEnabled method is true. If a dialog is display it will fail.
     *
     * @param webElement
     * @param waitForNotEnabled, Will wait for an elements to not be enabled
     */
    public void waitForWebElementIsEnabled(WebElement webElement, final boolean waitForNotEnabled) throws Exception {
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())))
                        .until(new ExpectedCondition<Boolean>() {
                            public Boolean apply(WebDriver d) {
                                if (waitForNotEnabled) {
                                    return !isElementEnabled(webElement);
                                } else {
                                    return isElementEnabled(webElement);
                                }
                            }
                        });
                break;
            } catch (StaleElementReferenceException e) {
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(1000);
                        continue;
                    } catch (Exception e1) {
                    }
                    Logger.info("handling stale elements reference: retry count-" + retryCount);
                }
                throw new Exception("StaleElementException while clicking selenium elements", e);
            } catch (TimeoutException timeOut) {
                timeOut.printStackTrace();
                throw new Exception("Time out while waiting for an elements " + webElement + " to be enabled!" + timeOut);
            } catch (Exception exception) {
                throw new Exception("Failed to wait for elements to be enabled. " + exception, exception);
            }
        }
    }

    public void waitForWebElementIsEnabled(WebElement webElement, int timeOutInSec,
                                           final boolean waitForNotEnabled) throws Exception {
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                (new WebDriverWait(windowsDriver, timeOutInSec))
                        .until(new ExpectedCondition<Boolean>() {
                            public Boolean apply(WebDriver d) {
                                if (waitForNotEnabled) {
                                    return !isElementEnabled(webElement);
                                } else {
                                    return isElementEnabled(webElement);
                                }
                            }
                        });
                break;
            } catch (StaleElementReferenceException e) {
                Logger.error("Stale elements exception while waiting for elements to be enabled", e);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(1000);
                        continue;
                    } catch (Exception e1) {
                    }
                    Logger.info("handling stale elements reference: retry count-" + retryCount);
                }
                throw new Exception("StaleElementException while waiting for selenium elements to be enabled", e);
            } catch (TimeoutException timeOut) {
                timeOut.printStackTrace();
                throw new Exception("Time out while waiting for an elements " + webElement + " to be enabled!" + timeOut);
            } catch (Exception exception) {
                throw new Exception("Failed to wait for elements to be enabled. " + exception, exception);
            }
        }
    }

    public void waitForWebElementIsEnabled(WebElement webElement, int timeOutInSec) throws Exception {
        waitForWebElementIsEnabled(webElement, timeOutInSec, false);
    }

    public void waitForWebElementIsEnabled(WebElement webElement) throws Exception {
        waitForWebElementIsEnabled(webElement, false);
    }

    /**
     * Clicks on a input button by id. This method will wait until the specified button is enabled.
     *
     * @param buttonId The id of the button
     */
    public void waitAndClickInputButtonById(final String buttonId) throws Exception {
        (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())))
                .until(new ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver d) {
                        try {
                            // Should add checks here to make sure only a single button is selected.
                            WebElement button = d.findElement(By.id(buttonId));
                            if (button.isEnabled()) {
                                clickElementAfterFocus(button);
                                return true;
                            }
                            return false;
                        } catch (Throwable t) {
                            return false;
                        }
                    }
                });
    }

    /**
     * Will wait for an entry to appear in a dropdown list
     *
     * @param element       A elements for a select web elements.
     * @param entry         The entry either by display name or value
     * @param isDisplayName If true entry is a display name, if false it's a value
     */
    public void waitForItemInOptionSelect(final WebElement element, final String entry,
                                          final boolean isDisplayName) throws Exception {

        (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())))
                .until(new ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver d) {
                        try {

                            Select selector = new Select(element);
                            if (isDisplayName) {
                                selector.selectByVisibleText(entry);
                            } else {
                                selector.selectByValue(entry);
                            }

                        } catch (StaleElementReferenceException e) {
                            // if the select is being updated while we are doing this
                            // return false so that we will retry
                            return false;
                        } catch (Exception e) {
                            Logger.error("caught exception while waiting for a select to have a value, could be ok...", e);
                            return false;
                        }
                        return true;
                    }
                });
    }

    /**
     * Verifies the Actual Text as compared to the Expected Text.
     *
     * @param by byLocator
     * @return boolean true if there are more than 0 elements on the page
     */
    public boolean isElementPresent(By by) {
        return windowsDriver.findElements(by).size() != 0;
    }

    /**
     * Returns a boolean indicating whether the specified elements meets the specified criteria.
     *
     * @param element   A WebElement.
     * @param displayed A boolean, true to expect the elements to be displayed, false otherwise.
     * @param enabled   A boolean, true to expect the elements do be enabled, false otherwise.
     * @return A boolean, true if the specified elements meets the specified criteria, false otherwise.
     */
    public boolean isElementDisplayedAndEnabled(WebElement element, boolean displayed, boolean enabled) {
        boolean isDisplayed = isElementDisplayed(element);
        if (displayed != isDisplayed) {
            return false;
        }
        boolean isEnabled = isElementEnabled(element);
        return enabled == isEnabled;
    }

    public void doubleClickElementAfterFocus(WebElement element, boolean scrollToBottom) throws Exception {
        try {
            waitForWebElementIsEnabled(element, false);
            waitForElementTobeClickable(element);
            String jsExec = "arguments[0].focus();";
            if (scrollToBottom) {
                jsExec += " window.scrollBy(0, document.body.scrollHeight);";
            }

            JavascriptExecutor js = (JavascriptExecutor) windowsDriver;
            Actions action = new Actions(windowsDriver);
            try {
                js.executeScript(jsExec, element);
                action.moveToElement(element).doubleClick().perform();
                //elements.click();
            } catch (WebDriverException wde1) {
                try {
                    js.executeScript("window.scrollBy(0, 200);", element);
                    action.moveToElement(element).doubleClick().perform();
                    //elements.click();
                } catch (WebDriverException wde2) {
                    js.executeScript("window.scrollBy(0, -200);", element);
                    action.moveToElement(element).doubleClick().perform();
                }
            }
        } catch (Exception e) {
            throw new Exception("exception while clicking selenium elements!", e);
        }
    }

    public void doubleClickElementAfterFocus(WebElement element) throws Exception {
        doubleClickElementAfterFocus(element, false);
    }

    /*  *//**
     * Returns true if there's an elements on the current screen that's still loading.
     *
     * @return True if the page has a elements that's loading and false otherwise.
     *//*
    public boolean isElementLoading() throws Exception {
        // These dialogs may pop up while a table on the page is loading. Wait until it's done loading.
        try {
            if (isElementDisplayed(elementLocator.findElementById("waitMessage_div"))) {
                return true;
            }
        } catch (NoSuchElementException ex) {
            // OK -- if there's not wait dialog, it can't be showing
        }

        try {
            if (isElementDisplayed(elementLocator.findElementByXpath("//td[@class='Messages']"))) {
                return true;
            }
        } catch (NoSuchElementException ex) {
            // A message of this type was not found.
        }

        // Some pages have multiple message div tags.
        List<WebElement> messages = elementLocator.findElementsByXpath("//div[@class='Messages']");
        for (WebElement message : messages) {
            try {
                if (message.isDisplayed()) {
                    return true;
                }
            } catch (StaleElementReferenceException ex) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * Will wait until a specified By elements's isDisplayed method is true. If a dialog is display it will fail.
     *
     * @param elementBy
     * @param waitForNotDisplayed, Will wait for an elements to not be displayed
     */
    public void waitForWebElementIsDisplayed(final By elementBy, final boolean waitForNotDisplayed) throws Exception {
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())))
                        .until(new ExpectedCondition<Boolean>() {
                            public Boolean apply(WebDriver d) {

                                if (waitForNotDisplayed) {
                                    return !isElementDisplayed(elementBy);
                                } else {
                                    return isElementDisplayed(elementBy);
                                }
                            }
                        });
                break;
            } catch (StaleElementReferenceException e) {
                Logger.error("Stale elements exception while waiting for elements to be displayed", e);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(1000);
                        continue;
                    } catch (Exception e1) {
                    }
                    Logger.info("handling stale elements reference: retry count-" + retryCount);
                }
                throw new Exception("Stale elements exception while waiting for elements to be displayed", e);
            } catch (TimeoutException e) {
                Logger.error(elementBy + " is not present! its not either attached to the DOM, getting timeout", e);
                break;
            } catch (Exception e) {
                Logger.error(elementBy + " is not present! its not either attached to the DOM, getting timeout", e);
                break;
            }
        }
    }

    public void waitForWebElementIsDisplayed(final By elementBy, int timeOutInSec,
                                             final boolean waitForNotDisplayed) throws Exception {
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                (new WebDriverWait(windowsDriver, timeOutInSec))
                        .until(new ExpectedCondition<Boolean>() {
                            public Boolean apply(WebDriver d) {

                                if (waitForNotDisplayed) {
                                    return !isElementDisplayed(elementBy);
                                } else {
                                    return isElementDisplayed(elementBy);
                                }
                            }
                        });
                break;
            } catch (StaleElementReferenceException e) {
                Logger.error("Stale elements exception while waiting for elements to be displayed", e);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(1000);
                        continue;
                    } catch (Exception e1) {
                    }
                    Logger.info("handling stale elements reference: retry count-" + retryCount);
                }
                throw new Exception("Stale elements exception while waiting for elements to be displayed", e);
            } catch (TimeoutException e) {
                Logger.error(elementBy + " is not present! its not either attached to the DOM, getting timeout", e);
                break;
            } catch (Exception e) {
                Logger.error(elementBy + " is not present! its not either attached to the DOM, getting timeout", e);
                break;
            }
        }
    }

    public void waitForWebElementIsDisplayed(final By elementBy, int timeOutInSec) throws Exception {
        waitForWebElementIsDisplayed(elementBy, timeOutInSec, false);
    }

    /**
     * Will wait until a specified By elements's isDisplayed method is true. If a dialog is display it will fail.
     *
     * @param elementBy By elements
     */
    public void waitForWebElementIsDisplayed(final By elementBy) throws Exception {
        waitForWebElementIsDisplayed(elementBy, false);
    }

    /**
     * Method to return if elements is displayed or not, even if the elements can not be found. If not found returns
     * false.
     *
     * @param byElement byLocator
     * @return boolean result
     */
    public boolean isElementDisplayed(By byElement) {
        boolean isDisplayed;
        try {
            isDisplayed = windowsDriver.findElement(byElement).isDisplayed();
        } catch (Throwable e) {
            isDisplayed = false;
        }
        return isDisplayed;
    }

    public void waitForElementTobeClickable(By byElement) {
        WebDriverWait wait = new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut()));
        wait.until(ExpectedConditions.elementToBeClickable(byElement));
    }

    public void waitForElementTobeClickable(WebElement element) {
        WebDriverWait wait = new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut()));
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitForElementNotTobeClickable(WebElement element) {
        WebDriverWait wait = new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut()));
        wait.until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(element)));
    }


    public void hitEnter(WebElement element) {
        element.sendKeys(Keys.ENTER);
    }


    public String getAttribute(By byElement, String attribute) throws Exception {
        String attrVal = null;
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                waitForWebElementIsDisplayed(byElement, false);
                attrVal = windowsDriver.findElement(byElement).getAttribute(attribute);
                break;
            } catch (StaleElementReferenceException sere) {
                Logger.error("stale elements exception while getting elements's:" + byElement + " attribute: " + attribute, sere);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(20000);
                        Logger.info("handling stale elements reference: retry count-" + retryCount);
                        continue;
                    } catch (Exception e1) {
                    }
                }
                throw new Exception("stale elements exeption while getting elements's:" + byElement + " attribute: " + attribute, sere);

            } catch (Exception e) {
                throw new Exception("Exception while getting elements:" + byElement + " attribute: " + attribute, e);
            }
        }
        return attrVal;
    }

    public String getAttribute(WebElement byElement, String attribute) throws Exception {
        String attrVal = null;
        int retryCount = 5;
        while (retryCount > 0) {
            try {
                waitForWebElementIsDisplayed(byElement, false);
                attrVal = byElement.getAttribute(attribute);
                break;
            } catch (StaleElementReferenceException sere) {
                Logger.error("stale elements exception while getting elements's:" + byElement + " attribute: " + attribute, sere);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(20000);
                        Logger.info("handling stale elements reference: retry count-" + retryCount);
                        continue;
                    } catch (Exception e1) {
                    }
                }
                throw new Exception("stale elements exeption while getting elements's:" + byElement + " attribute: " + attribute, sere);
            } catch (Exception e) {
                throw new Exception("Exception while getting elements:" + byElement + " attribute: " + attribute, e);
            }
        }
        return attrVal;
    }

    public void rightClick(WebElement element) {
        Actions act = new Actions(windowsDriver);
        act.moveToElement(element).perform();
        act.contextClick().perform();
    }

    public void dragAndDrop(WebElement from, WebElement to) {
        Actions act = new Actions(windowsDriver);
        act.dragAndDrop(from, to).build().perform();
    }
}
