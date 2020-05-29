package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import java.awt.Color;

import pl.edu.pw.stud.bialek2.marcin.proz.views.password.PasswordWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.password.PasswordWindowListener;


public class PasswordController implements PasswordWindowListener {
    private PasswordControllerListener listener;
    private PasswordWindow view;

    public PasswordController(PasswordWindow view) {
        this.view = view;
        this.view.setListener(this);
    }

    public void setListener(PasswordControllerListener listener) {
        this.listener = listener;
    }

    public void setPasswordIncorrect() {
        this.view.setPasswordFieldBorderColor(Color.RED);
    }

    public void closeWindow() {
        this.view.setVisible(false);
        this.view.dispose();
    }

    @Override
    public void passwordWindowDidClose() {
        if(this.listener != null) {
            this.listener.passwordControllerDidExit(this);
        }
    }

    @Override
    public void passwordWindowDidSubmit(char[] password) {
        if(this.listener != null) {
            this.listener.passwordControllerDidEnterPassword(this, password);
        }
    }
}