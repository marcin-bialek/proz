package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.services.SecurityService;
import pl.edu.pw.stud.bialek2.marcin.proz.services.UserService;
import pl.edu.pw.stud.bialek2.marcin.proz.views.setup.SetupWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.setup.SetupWindowListener;

import java.awt.event.KeyEvent;

import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class SetupController implements SetupWindowListener {
    private SetupControllerListener listener;
    private SetupWindow view;
    private boolean isNickValid = false;
    private boolean isPasswordValid = false;

    public SetupController(SetupWindow view) {
        this.view = view;
        this.view.setListener(this);
    }

    public void setListener(SetupControllerListener listener) {
        this.listener = listener;
    }

    public void closeWindow() {
        this.view.setVisible(false);
        this.view.dispose();
    }

    @Override
    public void setupWindowDidClose() {
        if(this.listener != null) {
            this.listener.setupControllerDidExit(this);
        }
    }

    @Override
    public void setupWindowDidSubmit(String nick, char[] password) {
        if(this.listener != null && this.isNickValid && this.isPasswordValid) {
            this.listener.setupControllerDidSetup(this, nick, password, App.DEFAULT_PORT);
        }
    }

    @Override
    public void setupWindowDidNickChange(String nick) {
        this.isNickValid = UserService.isValidNick(nick);
        this.view.setSubmitButtonEnabled(this.isNickValid && this.isPasswordValid);
    }

    @Override
    public void setupWindowDidPasswordChange(char[] password) {
        this.isPasswordValid = SecurityService.isValidPassword(password);
        this.view.setSubmitButtonEnabled(this.isNickValid && this.isPasswordValid);
    }
}