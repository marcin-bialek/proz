package pl.edu.pw.stud.bialek2.marcin.proz.controllers;


public interface PortTakenControllerDelegate {
    public void portTakenControllerDidExit(PortTakenController sender);
    public void portTakenControllerUsePortOnce(PortTakenController sender, int port);
    public void portTakenControllerUsePortAlways(PortTakenController sender, int port);
}
