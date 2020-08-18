@UI
Feature: Google Search

  @UI
  @google_search
  Scenario Outline: Search
    Given launch "<application>" on "<browser>"
    When enter "<search item>" in the search box
    And click on Google Search button
    Then "<search item> - Google Search" should be mentioned in the results
    Examples:
      | search item | browser | application            |
      | cats        | chrome  | https://www.google.com |
