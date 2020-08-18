package com.unicorn.stepdefinitions.calculator;

import com.unicorn.base.logger.Logger;
import com.unicorn.report.AllureReportManager;
import com.unicorn.pages.calculator.CalculatorPage;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;


public class CalculatorSteps {
    private CalculatorPage calcPage;

    public CalculatorSteps() {
        calcPage = new CalculatorPage();
    }

    @When("calculate {string} {string} {string}")
    public void calculate(String value1, String operator, String value2) {
        calcPage.performCalculate(value1,value2,operator);
    }

    @Then("verify answer from calculator is {string}")
    public void verifyAnswerOnCalculator(String answer) {
        Assert.assertEquals(answer,calcPage.GetCalculatorResultText());
        AllureReportManager.addScreenshotToAllureReport("calculator-result");
        Logger.info("verify answer from calculator is "+answer);
    }

}
