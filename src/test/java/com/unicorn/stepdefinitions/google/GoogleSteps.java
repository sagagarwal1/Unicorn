package com.unicorn.stepdefinitions.google;

import com.unicorn.base.logger.Logger;
import com.unicorn.pages.google.GooglePage;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class GoogleSteps {
    private GooglePage googlePage;

    public GoogleSteps() {
        googlePage = new GooglePage();
    }

    @When("enter {string} in the search box")
    public void enterValueInSearchBox(String searchItem) {
        googlePage.searchByValue(searchItem);
        Logger.info("Entered value "+searchItem+" in search box");
    }

    @And("click on Google Search button")
    public void clickOnButton() {
        googlePage.clickOnGoogleSearchButton();
        Logger.info("Clicked on Google Search button");
    }

    @Then("{string} should be mentioned in the results")
    public void verifyResultsPageTitle(String title) {
        googlePage.validatePageTitle(title);
        Logger.info("Verify title as: "+title);
    }

}
