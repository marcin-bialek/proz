package pl.edu.pw.stud.bialek2.marcin.proz.controllers;

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
        
    }

    @Override
    public void portTakenWindowDidPortChange(String port) {
        
    }

    @Override
    public void portTakenWindowDidClickUseOnceButton(String port) {
        final int portInt = Integer.parseInt(port);

        if(this.delegate != null) {
            this.delegate.portTakenControllerUsePortOnce(this, portInt);
        }
    }

    @Override
    public void portTakenWindowDidClickUseAlwaysButton(String port) {
        final int portInt = Integer.parseInt(port);

        if(this.delegate != null) {
            this.delegate.portTakenControllerUsePortAlways(this, portInt);
        }
    }
 }
