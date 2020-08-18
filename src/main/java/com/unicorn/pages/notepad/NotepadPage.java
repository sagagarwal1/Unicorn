package com.unicorn.pages.notepad;

import com.unicorn.pages.BasePage;
import org.openqa.selenium.WebElement;

import java.io.File;


public class NotepadPage extends BasePage {


    private WebElement getTextArea() {
        return windowsElementLocator.findElementByName("Text Editor");
    }

    private WebElement getSaveAsPathTextBox() {
        return windowsElementLocator.findElementByXpath("//*[@AutomationId='1001' and @Name='File name:']");
    }

    public void navigate(String path) {
        String[] menuItems = path.split(">");
        for (String item : menuItems) {
            windowsElements.clickElementAfterFocus(windowsElementLocator.findElementByName(item.trim()));
        }
    }

    /**
     * This will write content in NotePadTextArea
     */
    public void writeNote(String note) {
        windowsTextBox.sendKeys(getTextArea(), note);
    }


    public void saveFile(String path, String fileName, Boolean overwriteIfExists) {
        navigate("Application > File > Save As...\tCtrl+Shift+S");
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        windowsTextBox.sendKeys(getSaveAsPathTextBox(), path + File.separator + fileName);
        windowsElements.clickElementAfterFocus(windowsElementLocator.findElementByName("Save"));
        if (overwriteIfExists) {
            try {
                windowsElementLocator.findElementByName("Yes",0).click();
            } catch(Exception e){
            }
        }
    }

}
