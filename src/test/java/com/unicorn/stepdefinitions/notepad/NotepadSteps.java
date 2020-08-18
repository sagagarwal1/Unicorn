package com.unicorn.stepdefinitions.notepad;

import com.unicorn.base.logger.Logger;
import com.unicorn.pages.notepad.NotepadPage;
import cucumber.api.java.en.When;

import java.io.File;


public class NotepadSteps {
    private NotepadPage notepadPage;

    public NotepadSteps() {
        notepadPage = new NotepadPage();
    }

    @When("write note {string}")
    public void note(String note) {
        notepadPage.writeNote(note);
        Logger.info("Note: "+note);
    }

    @When("save file to path {string} as {string}")
    public void save(String path, String fileName) {
        notepadPage.saveFile(path,fileName,true);
        Logger.info("Saved file to path: "+path+ File.separator+fileName);
    }

}
