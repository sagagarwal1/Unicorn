package com.unicorn.pages.calculator;

import com.unicorn.base.BaseTestSetup;
import com.unicorn.base.logger.Logger;
import com.unicorn.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.testng.Assert;


public class CalculatorPage extends BasePage {


    private WebElement getOperatorButton(String operator) {
        switch (operator.toUpperCase()) {
            case "ADD":
            case "+":
                operator = "Add";
                break;
            case "SUBTRACT":
            case "-":
                operator = "Subtract";
                break;
            case "MULTIPLY":
            case "MULTIPLY BY":
            case "*":
                operator = "Multiply";
                break;
            case "DIVIDE":
            case "DIVIDE BY":
            case "/":
                operator = "Divide";
                break;
            default:
                Logger.assertFail("Please pass valid operator");
                break;
        }
        return windowsElementLocator.findElementByName(operator);
    }

    private WebElement getWindowCloseButton() {
        return windowsElementLocator.findElementByName("Close");
    }

    private WebElement getEqualsButton() {
        return windowsElementLocator.findElementByName("Equals");
    }

    private String splitInputs(String input) {
        String[] alphabets = input.split("");
        String sequence = "";
        for (String alphabet : alphabets) {
            switch (alphabet) {
                case "1":
                    sequence += "1,";
                    break;
                case "2":
                    sequence += "2,";
                    break;
                case "3":
                    sequence += "3,";
                    break;
                case "4":
                    sequence += "4,";
                    break;
                case "5":
                    sequence += "5,";
                    break;
                case "6":
                    sequence += "6,";
                    break;
                case "7":
                    sequence += "7,";
                    break;
                case "8":
                    sequence += "8,";
                    break;
                case "9":
                    sequence += "9,";
                    break;
                case "0":
                    sequence += "0,";
                    break;
                default:
                    Logger.assertFail("Please pass numeric values in input");
                    break;
            }
        }
        return sequence.substring(0, sequence.length() - 1);
    }

    public void performCalculate(String input1, String input2, String operator) {
        Logger.info("Calculate " + input1 + "" + operator + "" + input2);
        String result = null;
        try {
            String[] sequence1 = splitInputs(input1).split(",");
            String[] sequence2 = splitInputs(input2).split(",");
            windowsElements.clickElementAfterFocus(windowsElementLocator.findElementByName("Clear"));
            for (String number : sequence1) {
                Thread.sleep(500);
                windowsElements.clickElementAfterFocus(windowsElementLocator.findElementByName(number));
            }
            Thread.sleep(1000);
            windowsElements.clickElementAfterFocus(getOperatorButton(operator));
            Thread.sleep(1000);
            for (String number : sequence2) {
                Thread.sleep(500);
                windowsElements.clickElementAfterFocus(windowsElementLocator.findElementByName(number));
            }
            Thread.sleep(2000);
            windowsElements.clickElementAfterFocus(getEqualsButton());
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to Calculate", e);
        }
    }

    /**
     * This will close application
     */
    public void closeCalculator() {
        windowsElements.clickElementAfterFocus(getWindowCloseButton());
        BaseTestSetup.shutDownApplication("Calculator");
    }

    public String GetCalculatorResultText() {
        // trim extra text and whitespace off of the display value
        return windowsElementLocator.findElementByName("Result").getText().trim();
    }
}
