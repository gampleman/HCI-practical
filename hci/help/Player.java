package hci.help;
// Fig 21.6: MediaPanel.java
// A JPanel the plays media from a URL
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.io.File;
import javax.swing.JPanel;
import java.awt.Desktop;

public class Player extends JPanel
{
   public Player()
   {
       try {
         File mediaURL = new File("hci/help/Screencast.mov");
        setLayout( new BorderLayout() ); // use a BorderLayout
        Desktop desktop = Desktop.getDesktop();
        desktop.open(mediaURL);
      
       } catch(java.io.IOException e) {
         e.printStackTrace();
       }
      
   } // end MediaPanel constructor
} // end class MediaPanel
