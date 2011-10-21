package hci;

/**
 * adopted from http://download.oracle.com/javase/tutorial/uiswing/components/filechooser.html
 */

import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.activation.MimetypesFileTypeMap;

public class SplashScreen extends JPanel implements ActionListener {
  
  ImageLabeller labeler = new ImageLabeller();
    
    // UI Buttons
    JButton openButton, player;
    JFileChooser fc;
    // For autoclosing this window
    static JFrame frame;
    
    public SplashScreen() {
        super(new BorderLayout());
        fc = new JFileChooser();
        openButton = new JButton("Start by opening an image");
        openButton.addActionListener(this);
        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); //use FlowLayout
        JLabel label = new JLabel("Welcome to Labeler");
        label.setFont(new Font("SansSerif", java.awt.Font.BOLD, 18));
        buttonPanel.add(label);
        buttonPanel.add(openButton);

        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        // Create the video button
        player = new JButton(createImageIcon("help/poster.png"));
        player.addActionListener(this);
        add(player, BorderLayout.CENTER);
    }
    
    /**
     * Event handler for the buttons
     */
    public void actionPerformed(ActionEvent e) {
      FileNameMap fileNameMap = URLConnection.getFileNameMap();
        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String mimeType = fileNameMap.getContentTypeFor(file.getName());
                //whether the opened file is an image
                if (mimeType.startsWith("image")) {                   
                  try {
                    labeler.setupGUI(file.getPath());
                    frame.setVisible(false);
                  } catch (Exception er) {
                    er.printStackTrace();
                  }
                } else{
                  JOptionPane.showMessageDialog(null, "Please choose an image file!");
                }
                
            } 
        } else if(e.getSource() == player) {
          try {
             File mediaURL = new File("hci/help/Screencast.mkv");
            Desktop desktop = Desktop.getDesktop();
            desktop.open(mediaURL);
           } catch(java.io.IOException er) {
             er.printStackTrace();
           }
        }
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
      java.net.URL imgURL = SplashScreen.class.getResource(path);
      if (imgURL != null) {
        return new ImageIcon(imgURL);
      } else {
        System.err.println("Couldn't find file: " + path);
        return null;
      }
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Welcome to Labeler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new SplashScreen());
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                createAndShowGUI();
            }
        });
    }
}
