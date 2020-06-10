package pl.edu.pw.stud.bialek2.marcin.proz.views;


public interface PortTakenWindowListener {
    public void portTakenWindowDidClose();
    public void portTakenWindowDidPortChange(String port);
    public void portTakenWindowDidClickUseOnceButton(String port);
    public void portTakenWindowDidClickUseAlwaysButton(String port);
}
