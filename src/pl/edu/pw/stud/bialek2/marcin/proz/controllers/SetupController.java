package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.services.P2PService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserService;
import pl.edu.pw.stud.bialek2.marcin.proz.views.SetupWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.SetupWindowListener;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class SetupController implements SetupWindowListener {
    private SetupControllerDelegate delegate;
    private SetupWindow view;
    private boolean isNickValid = false;
    private boolean isPasswordValid = false;
    private boolean isPortValid = true;
    private boolean isDBFilenameValid = true;

    public SetupController(SetupWindow view) {
        this.view = view;
        this.view.setListener(this);
    }

    public void setDelegate(SetupControllerDelegate delegate) {
        this.delegate = delegate;
    }

    public void closeWindow() {
        this.view.setVisible(false);
        this.view.dispose();
    }

    private boolean areFieldsValid() {
        return this.isNickValid && this.isPasswordValid && this.isPortValid && this.isDBFilenameValid;
    }

    @Override
    public void setupWindowDidClose() {
        if(this.delegate != null) {
            this.delegate.setupControllerDidExit(this);
        }
    }

    @Override
    public void setupWindowDidSubmit(String nick, char[] password, String port, String dbFilename) {
        if(this.delegate != null && this.areFieldsValid()) {
            this.view.setSubmitButtonEnabled(false);
            this.view.setSubmitButtonText("Ładowanie...");
            this.delegate.setupControllerDidSetup(this, nick.trim(), password, Integer.parseInt(port.trim()), dbFilename.trim());
        }
    }

    @Override
    public void setupWindowDidNickChange(String nick) {
        this.isNickValid = UserService.isValidNick(nick.trim());
        this.view.setNickFieldViewBackgroundColor(this.isNickValid ? Color.WHITE : App.LIGHT_RED_COLOR);
        this.view.setSubmitButtonEnabled(this.areFieldsValid());
    }

    @Override
    public void setupWindowDidPasswordChange(char[] password) {
        this.isPasswordValid = SecurityService.isValidPassword(password);
        this.view.setPasswordFieldViewBackgroundColor(this.isPasswordValid ? Color.WHITE : App.LIGHT_RED_COLOR);
        this.view.setSubmitButtonEnabled(this.areFieldsValid());
    }

    @Override
    public void setupWindowDidPortChange(String port) {
        try {
            int portInt = Integer.parseInt(port.trim());
            this.isPortValid = P2PService.isValidPort(portInt);
        }
        catch(NumberFormatException e) {
            this.isPortValid = false;
        }

        this.view.setPortFieldViewBackgroundColor(this.isPortValid ? Color.WHITE : App.LIGHT_RED_COLOR);
        this.view.setSubmitButtonEnabled(this.areFieldsValid());
    }

    @Override
    public void setupWindowDidDatabaseFileChange(String filename) {
        this.isDBFilenameValid = filename.trim().length() > 0;
        this.view.setDatabaseFieldViewBackgroundColor(this.isDBFilenameValid ? Color.WHITE : App.LIGHT_RED_COLOR);
        this.view.setSubmitButtonEnabled(this.areFieldsValid());
    }
}
