package com.unicorn.base.selenium;

import com.unicorn.base.BaseTestSetup;
import com.unicorn.utils.AutProperties;
import io.appium.java_client.MobileBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * This class is use to find the elements on web pages.
 * All method in this class use explicit wait to detect presence of element located.
 *
 */
public class WindowsElementLocator extends BaseTestSetup {

    /**
     * Find element using class name
     *
     * @param className class name
     * @return WebElement
     * @throws Exception
     */
    public WebElement findElementByClassName(String className) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfElementLocated(By.className(className))));
    }

    /**
     * Find element using css
     *
     * @param cssSelector css value
     * @return WebElement
     * @throws Exception
     */
    public WebElement findElementByCssSelector(String cssSelector) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector))));
    }

    /**
     * Find element using id
     *
     * @param id id value
     * @return WebElement
     * @throws Exception
     */
    public WebElement findElementById(String id) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfElementLocated(By.id(id))));
    }

    /**
     * Find element using css
     *
     * @param linkText link
     * @return WebElement
     * @throws Exception
     */
    public WebElement findElementByLinkText(String linkText) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfElementLocated(By.linkText(linkText))));
    }

    /**
     * Find element using partial text of link
     *
     * @param partialLinkText link text
     * @return WebElement
     * @throws Exception
     */
    public WebElement findElementByPartialLinkText(String partialLinkText) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText(partialLinkText))));
    }

    /**
     * Find element using name value of element
     *
     * @param name name value
     * @return WebElement
     * @throws Exception
     */
    public WebElement findElementByName(String name) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfElementLocated(By.name(name))));
    }

    public WebElement findElementByName(String name, int timeoutInSeconds) {
        return (new WebDriverWait(windowsDriver, timeoutInSeconds).until(ExpectedConditions.presenceOfElementLocated(By.name(name))));
    }

    /**
     * Find element using html tag
     *
     * @param tag tag name
     * @return WebElement
     * @throws Exception
     */
    public WebElement findElementByTagName(String tag) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfElementLocated(By.tagName(tag))));
    }

    /**
     * Find element using element's xpath
     *
     * @param xpath xpath to element
     * @return WebElement
     * @throws Exception
     */
    public WebElement findElementByXpath(String xpath) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath))));
    }

    public WebElement findElementByAccessibilityId(String id) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfElementLocated(MobileBy.AccessibilityId(id))));
    }

    /**
     * This will find all elements on the page by class name value
     *
     * @param className class name value
     * @return List of WebElement
     * @throws Exception
     */
    public List<WebElement> findElementsByClassName(String className) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(className))));
    }

    /**
     * This will find all elements on the page by css value
     *
     * @param cssSelector css value
     * @return List of WebElement
     * @throws Exception
     */
    public List<WebElement> findElementsByCssSelector(String cssSelector) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(cssSelector))));
    }

    /**
     * This will find all elements on the page by id value
     *
     * @param id value
     * @return List of WebElement
     * @throws Exception
     */
    public List<WebElement> findElementsById(String id) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(id))));
    }

    /**
     * This will find all elements on the page by html tag name
     *
     * @param tag html tag
     * @return List of WebElement
     * @throws Exception
     */
    public List<WebElement> findElementsByTagName(String tag) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName(tag))));
    }

    /**
     * This will find all elements on the page by element xpath
     *
     * @param xpath xpath
     * @return List of WebElement
     * @throws Exception
     */
    public List<WebElement> findElementsByXpath(String xpath) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(xpath))));
    }

    public List<WebElement> findElementsByAccessibilityId(String id) {
        return (new WebDriverWait(windowsDriver, Long.parseLong(AutProperties.getRequestTimeOut())).until(ExpectedConditions.presenceOfAllElementsLocatedBy(MobileBy.AccessibilityId(id))));
    }
}
