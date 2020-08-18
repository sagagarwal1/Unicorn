@UI
Feature: Notepad Application

  @UI
  @notepad
  Scenario Outline: Save Notes in Notepad
    Given launch "notepad" application
    When write note "<note>"
    And save file to path "<path>" as "<filename>"
    And close application "notepad"
    Examples:
      | path         | filename  | note                                                                                                |
      | C:\TestNotes | test1.txt | Western Union is an American worldwide financial services and communications company.               |
      | C:\TestNotes | test2.txt | Western Union was globally the best-known American company in the business of exchanging telegrams. |