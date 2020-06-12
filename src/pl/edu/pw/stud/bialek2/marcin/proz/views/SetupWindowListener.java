package pl.edu.pw.stud.bialek2.marcin.proz.views;


public interface SetupWindowListener {
    public void setupWindowDidClose();  
    public void setupWindowDidSubmit(String nick, char[] password, String port, String dbFilename);   
    public void setupWindowDidNickChange(String nick);
    public void setupWindowDidPasswordChange(char[] password); 
    public void setupWindowDidPortChange(String port);
    public void setupWindowDidDatabaseFileChange(String filename);
}
