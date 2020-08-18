package com.unicorn.base.selenium;

import com.unicorn.base.BaseTestSetup;
import com.unicorn.base.logger.Logger;
import org.openqa.selenium.*;
import org.testng.Assert;

public class JavaScriptExecutor extends BaseTestSetup {

    private WebDriver driver;

    public JavaScriptExecutor() {
        this.driver = webDriver;
    }

    /**
     * overloaded click method using By syntax
     *
     * @param by By value
     */
    public void click(By by) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("arguments[0].click();", by);
        } catch (Exception e) {
            Assert.fail("Exception while clicking of elements failed using java-script-" + by, e);
        }
    }

    /**
     * overloaded click method using Webelement
     *
     * @param element WebElement
     */
    public void click(WebElement element) {
        int retryCount = 5;
        JavascriptExecutor js = (JavascriptExecutor) driver;
        while (retryCount > 0) {
            try {
                js.executeScript("arguments[0].click();", element);
                break;
            } catch (StaleElementReferenceException e) {
                Logger.error("stale elements exception while clicking on elements: " + element);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(20000);
                        Logger.error("Handling stale elements exception, retry-count: " + retryCount);
                        continue;
                    } catch (Exception e1) {
                    }
                }
            } catch (Exception e) {
                Assert.fail("Exception while clicking of elements failed using javascript" + element, e);
            }
        }
    }

    /**
     * Clicks the specified By Element after having focused on it
     *
     * @param elemeBy Element to be clicked.
     */

   /* public void clickElementAfterFocus(By elemeBy) {
        clickElementAfterFocus(driver.findElement(elemeBy));
    }*/

    /**
     * Clicks the specified WebElement. This includes focus-handling logic to
     * work properly where the elements is not displayed, or elements in focus is
     * overlapped by another elements (ex: floating banner, menu bar, etc)
     *
     * @param element WebElement to be clicked.
     */
    public void clickElementAfterFocus(WebElement element) {
        clickElementAfterFocus(element, false);
    }

    /**
     * Clicks the specified WebElement. This includes focus-handling logic to
     * work properly where the elements is not displayed, or elements in focus is
     * overlapped by another elements (ex: floating banner, menu bar, etc)
     *
     * @param element        WebElement to be clicked.
     * @param scrollToBottom - If true, scroll to bottom of the page after shifting focus
     */
    public void clickElementAfterFocus(WebElement element, boolean scrollToBottom) {
        int retryCount = 5;
        String jsExec = "arguments[0].focus();";
        if (scrollToBottom) {
            jsExec += " window.scrollBy(0, document.body.scrollHeight);";
        }

        while (retryCount > 0) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            try {
                js.executeScript(jsExec, element);
                element.click();
                break;
            } catch (StaleElementReferenceException e) {
                Logger.error("stale elements exception while clicking on elements: " + element);
                retryCount--;
                if (retryCount != 0) {
                    try {
                        Thread.sleep(20000);
                        Logger.error("Handling stale elements exception, retry-count: " + retryCount);
                        continue;
                    } catch (Exception e1) {
                    }
                }
            } catch (WebDriverException wde1) {
                try {
                    js.executeScript("window.scrollBy(0, 200);", element);
                    element.click();
                } catch (WebDriverException wde2) {
                    js.executeScript("window.scrollBy(0, -200);", element);
                    element.click();
                }
            }
        }
    }

    /**
     * This will enter the values to text area and textbox.
     *
     * @param value   value to be entered
     * @param element WebElement
     */
    public void sendKeys(String value, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("arguments[0].value='" + value + "';", element);
        } catch (Exception e) {
            Assert.fail("Exception while sending keys using javascript. keys-\" + keys + \" & elements-\" + elements" + element, e);
        }
    }

    /**
     * This will execute provided javascript command on the specified element
     *
     * @param command command to execute
     * @param element elements to be used by command
     */
    public void executeJS(String command, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript(command, element);
        } catch (Exception e) {
            Assert.fail("Exception while executing javascript", e);
        }
    }

    /**
     * This method will focus on the elements within the specified html tag
     *
     * @param tag tag name to set focus
     */
    public void setFocusByTagName(String tag) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("document.getElementsByTagName('" + tag + "')[0].focus()");
        } catch (Exception e) {
            Assert.fail("Exception while setting focus by tag name using javascript", e);
        }
    }

    /**
     * This method will focus on the element within the specified name value of it
     *
     * @param name name of elements to set focus
     */
    public void setFocusByName(String name) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("document.getElementsByName('" + name + "')[0].focus()");
        } catch (Exception e) {
            Assert.fail("Exception while setting focus by name using javascript", e);
        }
    }

    /**
     * This method will focus on the element within the specified id value of it
     *
     * @param id elements id to set focus
     */
    public void setFocusById(String id) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("document.getElementById('" + id + "')[0].focus()");
        } catch (Exception e) {
            Assert.fail("Exception while setting focus by ID using javascript");
        }
    }


    /**
     * This method will scroll the web page till the given element comes into view.
     *
     * @param element WebElement
     */
    public void scrollIntoView(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("arguments[0].scrollIntoView(true);", element);
        } catch (Exception e) {
            Assert.fail("Exception while scrolling into view using javascript", e);
        }
    }

    /**
     * This method scroll the web page to the extreme top
     */
    public void scrollToTop() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("window.scrollTo(0,0);");
        } catch (Exception e) {
            Assert.fail("Exception while scrolling to top using javascript", e);
        }
    }

    /**
     * This method will set the view of web page to the given width and height of page
     *
     * @param width
     * @param height
     */
    public void scrollBy(String width, String height) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            js.executeScript("window.scrollTo(" + width + "," + height + ");");
        } catch (Exception e) {
            Assert.fail("Exception while scrolling using javascript", e);
        }
    }


    /**
     * This method will execute javaScript into the page
     *
     * @param script script
     */
    public Object executeScript(String script) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            return js.executeScript(script);
        } catch (Exception e) {
            Assert.fail("Exception while executing javascript", e);
        }
        return null;
    }

    /**
     * This method will execute javaScript asynchronously into the page
     *
     * @param script script
     */
    public Object executeAsyncScript(String script) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            return js.executeAsyncScript(script);
        } catch (Exception e) {
            Assert.fail("Exception while executing javascript", e);
        }
        return null;
    }


    /**
     * his method will execute javaScript into the page
     *
     * @param script script
     * @param args   argument list
     */
    public Object executeScript(String script, Object... args) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            return js.executeScript(script, args);
        } catch (Exception e) {
            Assert.fail("Exception while executing javascript", e);
        }
        return null;
    }

}
