package com.unicorn.base.selenium;

import com.unicorn.base.BaseTestSetup;
import com.unicorn.base.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BrowserWindows extends BaseTestSetup {

    public static ArrayList<String> allSessions = new ArrayList();

    /**
     * Get current window handle
     *
     * @return current window state
     */
    public String getCurrentWindowHandle() {
        return webDriver.getWindowHandle();
    }

    /**
     * Get all opened windows state
     *
     * @return Set of all opened windows state
     */
    public Set<String> getAllWindowsHandle() {
        return webDriver.getWindowHandles();
    }

    /**
     * Switch to another window state(to the 2nd opened window if more than 2 windows has opened) other than current window
     *
     * @return switched window handle
     * @throws Exception if no another instance of window found.
     */
    public String switchWindowHandleToNewestWindow() {
        Set openedBrowserSessions;
        Iterator iterator;
        allSessions.add(getCurrentWindowHandle());

        List<String> list = new ArrayList<String>(getAllWindowsHandle());
        for (String session : allSessions) {
            if (!list.contains(session)) {
                allSessions.remove(session);
            }
        }

        int count = 0;
        do {
            count++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            openedBrowserSessions = getAllWindowsHandle();
            // if (openedBrowserSessions.size() < 2) {
            if (count == 60)
                Logger.assertFail("no new window's session has opened, waited for a minute!");
            else
                Logger.info("Waiting for new window to open");
            Logger.info("openedBrowserSessions: " + openedBrowserSessions.size() + " and allSessions: " + allSessions.size());
        } while (openedBrowserSessions.size() == allSessions.size());

        iterator = openedBrowserSessions.iterator();
        String currentWindowReference = "";
        while (iterator.hasNext()) {
            currentWindowReference = iterator.next().toString();
            if (!allSessions.contains(currentWindowReference)) {
                Logger.info("Switching to new window");
                webDriver.switchTo().window(currentWindowReference);
                break;
            }
        }
        return currentWindowReference;
    }
}
