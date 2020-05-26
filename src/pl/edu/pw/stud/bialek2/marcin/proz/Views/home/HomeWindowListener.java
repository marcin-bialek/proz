package pl.edu.pw.stud.bialek2.marcin.proz.views.home;

import java.awt.Dimension;


public interface HomeWindowListener {
    public void homeWindowDidResize(Dimension windowSize);
    public void homeWindowDidClose();
    public void homeWindowDidClickNewChatroomButton();    
}
