//reference: http://download.oracle.com/javase/tutorial/uiswing/components/

package hci;

import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.activation.MimetypesFileTypeMap;
import hci.help.*;


/*
 * @author Jack
 * FileChooserDemo.java uses these files:
 *   images/Open16.gif
 *   images/Save16.gif
 */
public class launcher extends JPanel
                             implements ActionListener {
	
	ImageLabeller labeller = new ImageLabeller();
    static private final String newline = "\n";
    JButton openButton, saveButton, player;
    JTextArea log;
    JFileChooser fc;
    static JFrame frame;
    
    fileDirs dir = new fileDirs();

    public launcher() {
        super(new BorderLayout());

        //Create the log first, because the action listeners
        //need to refer to it.
        // log = new JTextArea(5,20);
        //        log.setMargin(new Insets(5,5,5,5));
        //        log.setEditable(false);
        //JScrollPane logScrollPane = new JScrollPane(log);

        //Create a file chooser
        fc = new JFileChooser();

        openButton = new JButton("Start by opening an image");
        openButton.addActionListener(this);

        //Create the save button.  We use the image from the JLF
        //Graphics Repository (but we extracted it from the jar).
        saveButton = new JButton("Save a File...",
                                 createImageIcon("images/Save16.gif"));
        saveButton.addActionListener(this);

        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); //use FlowLayout
        JLabel label = new JLabel("Welcome to Labeler");
        label.setFont(new Font("SansSerif", java.awt.Font.BOLD, 18));
        buttonPanel.add(label);
        buttonPanel.add(openButton);
        //buttonPanel.add(saveButton);

        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_START);
        
        //Player player = new Player();
        player = new JButton(createImageIcon("help/poster.png"));
        player.addActionListener(this);
        add(player, BorderLayout.CENTER);
        //add(logScrollPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent e) {
    	FileNameMap fileNameMap = URLConnection.getFileNameMap();
        //Handle open button action.
        if (e.getSource() == openButton) {
            int returnVal = fc.showOpenDialog(launcher.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String mimeType = fileNameMap.getContentTypeFor(file.getName());
                //whether the opened file is an image
                if (mimeType.startsWith("image")) { 
                	
                    try {
                    	dir.setImDir(file.getPath());
    					labeller.setupGUI(file.getPath());
    					frame.setVisible(false);
    				} catch (Exception er) {
    					// TODO Auto-generated catch block
    					er.printStackTrace();
    				}
                } else{
                	JOptionPane.showMessageDialog(null, "Please choose an image file!");
                }
                
            } else {
            }

        //Handle save button action.
        } else if (e.getSource() == saveButton) {
        	JOptionPane.showMessageDialog(null, "Please choose the directory and just name the file without type, the program will set the file type to 'label'");
        	
            int returnVal = fc.showSaveDialog(launcher.this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {	
                File file = fc.getSelectedFile();
                dir.setLbDir(file.getPath());

            } else {

            }
            log.setCaretPosition(log.getDocument().getLength());
        } else if(e.getSource() == player) {
          try {
             File mediaURL = new File("hci/help/Screencast.mov");
            Desktop desktop = Desktop.getDesktop();
            desktop.open(mediaURL);
           } catch(java.io.IOException er) {
             er.printStackTrace();
           }
        }
    }



    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = launcher.class.getResource(path);
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
        frame.add(new launcher());
        
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
