package com.unicorn.pages;

import com.unicorn.base.logger.Logger;
import com.unicorn.base.selenium.*;

public class BasePage {


    protected WindowsElements windowsElements;
    protected WindowsTextBox windowsTextBox;
    protected WindowsElementLocator windowsElementLocator;

    protected BrowserElements browserElements;
    protected BrowserElementLocator browserElementLocator;
    protected BrowserCheckBox browserCheckBox;
    protected BrowserTextBox browserTextBox;
    protected BrowserWindows browserWindows;
    protected JavaScriptExecutor javaScriptExecutor;


    public BasePage() {
        windowsElements = new WindowsElements();
        windowsElementLocator = new WindowsElementLocator();
        windowsTextBox = new WindowsTextBox();
        browserElements = new BrowserElements();

        browserElementLocator = new BrowserElementLocator();
        browserCheckBox = new BrowserCheckBox();
        browserTextBox = new BrowserTextBox();
        browserWindows = new BrowserWindows();
        javaScriptExecutor = new JavaScriptExecutor();
    }

    protected void wait(int miliSec) {
        try {
            Thread.sleep(miliSec);
        } catch (InterruptedException ie) {
            Logger.error("wait() interrupted!");
        }
    }
}
