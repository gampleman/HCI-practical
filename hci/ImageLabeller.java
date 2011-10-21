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
	
	/**
	 * handles New Object button action
	 */
	public void addNewPolygon() {
		//imagePanel.addNewPolygon();
	}
	
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
		  		//here we exit the program (maybe we should ask if the user really wants to do it?)
		  		//maybe we also want to store the polygons somewhere? and read them next time
		  		imagePanel.save();
		  		System.out.println("Bye bye!");
		    	System.exit(0);
		  	}
		});

		//setup main window panel
		//appPanel = new JPanel();
		
		//this.setLayout(new BoxLayout(appPanel, BoxLayout.X_AXIS));
		
		

       //      //create toolbox panel
      //     toolboxPanel = new JPanel();
      //         
      //         //Add button
      // JButton newPolyButton = new JButton("New object");
      // newPolyButton.setMnemonic(KeyEvent.VK_N);
      // newPolyButton.setSize(50, 20);
      // newPolyButton.setEnabled(true);
      // newPolyButton.addActionListener(new ActionListener() {
      //  @Override
      //  public void actionPerformed(ActionEvent e) {
      //        addNewPolygon();
      //  }
      // });
      // newPolyButton.setToolTipText("Click to add new object");
      // 
      // toolboxPanel.add(newPolyButton);
   		
		//add toolbox to window
		//appPanel.add(toolboxPanel);
		    //Create and set up the image panel.
		imagePanel = new ImagePanel(imageFilename);
		imagePanel.setOpaque(true); //content panes must be opaque
		Dimension panelSize = new Dimension(800, 600);
		this.setSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setMaximumSize(panelSize);
    //appPanel.add(imagePanel);
    this.setContentPane(imagePanel);
    
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
    menu.getAccessibleContext().setAccessibleDescription(
            "Open and load files...");
    menuBar.add(menu);

    //a group of JMenuItems
    menuItem = new JMenuItem("Open...",
                             KeyEvent.VK_T);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_O, ActionEvent.CTRL_MASK));
    menuItem.getAccessibleContext().setAccessibleDescription(
            "Open an image.");
    menu.add(menuItem);
    menuItem.addActionListener(this);

    menuItem = new JMenuItem("Save");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(
            KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    menu.add(menuItem);
    menuItem.addActionListener(this);
    // OSX apps need a help menu
    menu = new JMenu("Help");
    menu.setMnemonic(KeyEvent.VK_N);
    menu.getAccessibleContext().setAccessibleDescription(
            "This menu does nothing");
    menuBar.add(menu);
    


    setJMenuBar(menuBar);
    
    
		//display all the stuff
		this.pack();
        this.setVisible(true);
	}
	
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
      					// TODO Auto-generated catch block
      					er.printStackTrace();
      				}
            } else{
            	JOptionPane.showMessageDialog(null, "Please choose an image file!");
            }
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
