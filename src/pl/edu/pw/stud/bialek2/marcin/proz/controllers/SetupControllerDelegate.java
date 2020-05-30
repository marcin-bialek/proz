package pl.edu.pw.stud.bialek2.marcin.proz.controllers;


public interface SetupControllerDelegate {
    public void setupControllerDidExit(SetupController sender);
    public void setupControllerDidSetup(SetupController sender, String nick, char[] password, int port);
}

