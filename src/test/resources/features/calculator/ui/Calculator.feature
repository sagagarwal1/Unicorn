@UI
Feature: Verify Calculator Application

  @UI
  @calculator
  Scenario Outline: Verify Windows Calculator application
    Given launch "Calculator" application
    When calculate "<value1>" "<operator>" "<value2>"
    Then verify answer from calculator is "<answer>"
    And close application "Calculator"
    Examples:
      | value1 | operator | value2 | answer |
      | 12     | +        | 5      | 17     |
      | 15     | -        | 5      | 10     |