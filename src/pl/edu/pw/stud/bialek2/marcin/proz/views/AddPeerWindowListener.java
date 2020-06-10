package pl.edu.pw.stud.bialek2.marcin.proz.views;


public interface AddPeerWindowListener {
    public void addPeerWindowDidAddressChange(String address);
    public void addPeerWindowDidPortChange(String port);
    public void addPeerWindowDidAddPeer(String address, String port);
}
