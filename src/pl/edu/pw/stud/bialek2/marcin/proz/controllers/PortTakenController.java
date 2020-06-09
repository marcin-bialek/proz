package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

import java.awt.Color;

import pl.edu.pw.stud.bialek2.marcin.proz.App;
import pl.edu.pw.stud.bialek2.marcin.proz.services.P2PService;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PortTakenWindow;
import pl.edu.pw.stud.bialek2.marcin.proz.views.PortTakenWindowListener;


public class PortTakenController implements PortTakenWindowListener {
    private PortTakenControllerDelegate delegate;
    private PortTakenWindow view;

    public PortTakenController(PortTakenWindow view, int currentPort) {
        this.view = view;
        view.setCurrentPort(currentPort);
        view.setListener(this);
    }

    public void setDelegate(PortTakenControllerDelegate delegate) {
        this.delegate = delegate;
    }

    public void closeWindow() {
        this.view.setVisible(false);
        this.view.dispose();
    }

    @Override
    public void portTakenWindowDidClose() {
        if(this.delegate != null) {
            this.delegate.portTakenControllerDidExit(this);
        }
    }

    @Override
    public void portTakenWindowDidPortChange(String port) {
        boolean isPortValid;

        try {
            final int portInt = Integer.parseInt(port.trim());
            isPortValid = P2PService.isValidPort(portInt);
        }
        catch(NumberFormatException e) {
            isPortValid = false;
        }

        if(isPortValid) {
            this.view.setPortFieldBackgroundColor(Color.WHITE);
            this.view.setButtonsEnabled(true);
        }
        else {
            this.view.setPortFieldBackgroundColor(App.LIGHT_RED_COLOR);
            this.view.setButtonsEnabled(false);
        }
    }

    @Override
    public void portTakenWindowDidClickUseOnceButton(String port) {
        final int portInt = Integer.parseInt(port.trim());

        if(this.delegate != null) {
            this.delegate.portTakenControllerUsePortOnce(this, portInt);
        }
    }

    @Override
    public void portTakenWindowDidClickUseAlwaysButton(String port) {
        final int portInt = Integer.parseInt(port.trim());

        if(this.delegate != null) {
            this.delegate.portTakenControllerUsePortAlways(this, portInt);
        }
    }
 }
