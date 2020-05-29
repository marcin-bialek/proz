package pl.edu.pw.stud.bialek2.marcin.proz.views.setup;

import java.awt.event.KeyEvent;


public interface SetupWindowListener {
    public void setupWindowDidClose();  
    public void setupWindowDidSubmit(String nick, char[] password);   
    public void setupWindowDidNickChange(String nick);
    public void setupWindowDidPasswordChange(char[] password); 
}
