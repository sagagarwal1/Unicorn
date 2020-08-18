package com.unicorn.base.selenium;

import com.google.common.base.Function;
import com.unicorn.base.BaseTestSetup;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BrowserPage extends BaseTestSetup {
    private WebDriver driver;

    public BrowserPage() {
        this.driver = webDriver;
    }

    /**
     * Wait to page get fully loaded
     */
    public void waitForPageToLoad() {
        Wait<WebDriver> wait = new WebDriverWait(driver, 90);
        wait.until(new Function<WebDriver, Boolean>() {
            public Boolean apply(WebDriver driver) {
                return String
                        .valueOf(((JavascriptExecutor) driver).executeScript("return document.readyState"))
                        .equals("complete");
            }
        });
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("waitMessage_div")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(text(),'Loading')]")));
            // waitForPageToLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigate to forward page
     */
    public void navigateToForward() {
        driver.navigate().forward();
    }

    /**
     * Navigate to back page
     */
    public void navigateToBack() {
        driver.navigate().back();
    }

    /**
     * Refresh the current page and wait for page to get loaded fully
     */
    public void refreshPage() {
        driver.navigate().refresh();
        waitForPageToLoad();
    }

    /**
     * verify that scrollbar is present for the given web elements
     *
     * @param element WebElement
     */
    public boolean isScrollbarPresent(WebElement element) {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        return (Boolean) javaScriptExecutor.executeScript("return arguments[0].scrollHeight > arguments[0].clientHeight;",
                element);
    }

    /**
     * Scroll html page down by 1000 coordinated from current location
     */
    public void scrollDown() {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        Long value = (Long) javaScriptExecutor.executeScript("return window.pageYOffset;");
        javaScriptExecutor.executeScript("scroll(0, " + (value + 1000) + ");");
    }

    /**
     * Scroll html page up by 1000 coordinated from current location
     */
    public void scrollUp() {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        Long value = (Long) javaScriptExecutor.executeScript("return window.pageYOffset;");
        javaScriptExecutor.executeScript("scroll(0, " + (value - 1000) + ");");
    }

    /**
     * Push scroll-bar down and set on the given co-ordinates from the page top
     *
     * @param coordinatedFromTop co-ordinates to set scrollbar
     */
    public void scrollDown(long coordinatedFromTop) {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        javaScriptExecutor.executeScript("scroll(0, " + coordinatedFromTop + ");");
    }

    /**
     * Pull scroll-bar up and set on the given co-ordinates from the page top
     *
     * @param coordinatedFromTop co-ordinates to set scrollbar
     */
    public void scrollUp(long coordinatedFromTop) {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        javaScriptExecutor.executeScript("scroll(0, " + (-coordinatedFromTop) + ");");
    }


    public void scrollToBottom() {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        javaScriptExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    public void scrollToTop() {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        javaScriptExecutor.executeScript("window.scrollTo(0, -document.body.scrollHeight)");
    }

    public void scrollHorizontallyToRight() {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        Long value = (Long) javaScriptExecutor.executeScript("return window.pageXOffset;");
        javaScriptExecutor.executeScript("window.scrollBy(" + (value + 1000) + ",0)");
    }

    public void scrollHorizontallyToLeft() {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        Long value = (Long) javaScriptExecutor.executeScript("return window.pageXOffset;");
        javaScriptExecutor.executeScript("window.scrollBy(" + (value - 1000) + ",0)");
    }

    public void scrollHorizontallyToRight(long coordinatedFromExtremeLeft) {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        Long value = (Long) javaScriptExecutor.executeScript("return window.pageXOffset;");
        javaScriptExecutor.executeScript("window.scrollBy(" + coordinatedFromExtremeLeft + ",0)");
    }

    public void scrollHorizontallyToLeft(long coordinatedFromExtremeLeft) {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        Long value = (Long) javaScriptExecutor.executeScript("return window.pageXOffset;");
        javaScriptExecutor.executeScript("window.scrollBy(" + (-coordinatedFromExtremeLeft) + ",0)");
    }

    public void scrollToElement(WebElement element) {
        JavaScriptExecutor javaScriptExecutor = new JavaScriptExecutor();
        javaScriptExecutor.executeScript("arguments[0].scrollIntoView();", element);
    }
}
