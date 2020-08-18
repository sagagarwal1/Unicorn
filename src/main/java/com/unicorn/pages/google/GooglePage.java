package com.unicorn.pages.google;

import com.unicorn.base.BaseTestSetup;
import com.unicorn.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.testng.Assert;


public class GooglePage extends BasePage {

    public WebElement getSearchTextBox() {
        return browserElementLocator.findElementByXpath("//input[@title='Search']");
    }

    public WebElement getSearchButton() {
        return browserElementLocator.findElementByXpath("(//input[@value=\"Google Search\"])[1]");
    }

    public void searchByValue(String searchContent) {
        browserTextBox.sendKeys(getSearchTextBox(),searchContent);
    }

    public void clickOnGoogleSearchButton() {
        browserElements.clickElementAfterFocus(getSearchButton());
    }

    public void validatePageTitle(String expectedTitle) {
        String title=BaseTestSetup.webDriver.getTitle();
        Assert.assertTrue(browserElements.verifyText(title,expectedTitle),"Title validation - Actual: \""+title+"\" Expected:\""+expectedTitle+"\"");
    }
}
