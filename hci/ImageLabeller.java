package hci;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import java.awt.Dimension;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import javax.swing.JOptionPane;
import java.net.FileNameMap;
import java.net.URLConnection;
import javax.swing.filechooser.*;
import java.awt.Desktop;

/**
 * Main class of the program - handles display of the main window
 * @author Michal
 * @author Jakub
 *
 */
public class ImageLabeller extends JFrame implements ActionListener {
	/**
	 * some java stuff to get rid of warnings
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * image panel - displays image and editing area
	 */
	ImagePanel imagePanel = null;
	

	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		imagePanel.paint(g); //update image panel
	}
	
	/**
	 * sets up application window
	 * @param imageFilename image to be loaded for editing
	 * @throws Exception
	 */
	public void setupGUI(String imageFilename) throws Exception {
		this.addWindowListener(new WindowAdapter() {
		  	public void windowClosing(WindowEvent event) {
		  		imagePanel.save();
		    	System.exit(0);
		  	}
		});

	
		//Create and set up the image panel.
		imagePanel = new ImagePanel(imageFilename);
		imagePanel.setOpaque(true); //content panes must be opaque
		Dimension panelSize = new Dimension(800, 600);
		this.setSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setMaximumSize(panelSize);
    this.setContentPane(imagePanel);
    // Create the menus
    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;

    //Create the menu bar.
    menuBar = new JMenuBar();

    //Build the first menu.
    menu = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_F);
    menu.getAccessibleContext().setAccessibleDescription("Open and load files...");
    menuBar.add(menu);

    //a group of JMenuItems
    menuItem = new JMenuItem("Open...", KeyEvent.VK_T);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription("Open an image.");
    menu.add(menuItem);
    menuItem.addActionListener(this);

    menuItem = new JMenuItem("Save");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    menu.add(menuItem);
    menuItem.addActionListener(this);
    menuItem = new JMenuItem("Save As...");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
    menu.add(menuItem);
    menuItem.addActionListener(this);
    // OSX apps need a help menu
    menu = new JMenu("Help");
    menu.setMnemonic(KeyEvent.VK_N);
    menu.getAccessibleContext().setAccessibleDescription("Help menu");
    menuBar.add(menu);
    menuItem = new JMenuItem("Show Introductory Video");
    menu.add(menuItem);
    menuItem.addActionListener(this);
    setJMenuBar(menuBar);
    
    
		//display all the stuff
		this.pack();
    this.setVisible(true);
	}
	
	/**
	 * Event handler for the menus
	 */
	public void actionPerformed(ActionEvent e) {
    System.out.println("Menu: "+((JMenuItem)e.getSource()).getText());
    String txt = ((JMenuItem)e.getSource()).getText();
    if (txt == "Open...") {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        JFileChooser fc = new JFileChooser();;
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String mimeType = fileNameMap.getContentTypeFor(file.getName());
            if (mimeType.startsWith("image")) { 
              try {
					      setupGUI(file.getPath());
					    } catch (Exception er) {
      					er.printStackTrace();
      				}
            } else{
            	JOptionPane.showMessageDialog(null, "Please choose an image file!");
            }
        }

    } else if(txt == "Save") {
      imagePanel.save();
    } else if(txt == "Save As...") {
      JFileChooser fc = new JFileChooser();
      int returnVal = fc.showSaveDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = fc.getSelectedFile();
          imagePanel.saveAs(file.getPath());
      }
    } else if(txt == "Show Introductory Video") {
      try {
        File mediaURL = new File("hci/help/Screencast.mkv");
        Desktop desktop = Desktop.getDesktop();
        desktop.open(mediaURL);
       } catch(java.io.IOException er) {
         er.printStackTrace();
       }
    }
  }
	
	/**
	 * Runs the program
	 * @param argv path to an image
	 */
	public static void main(String argv[]) {
		try {
			//create a window and display the image
			ImageLabeller window = new ImageLabeller();
			window.setupGUI(argv[0]);
		} catch (Exception e) {
			System.err.println("Image: " + argv[0]);
			e.printStackTrace();
		}
	}
}
