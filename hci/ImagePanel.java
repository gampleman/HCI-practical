package hci;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.io.*;
import hci.utils.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
/**
 * Handles image editing panel
 * @author Michal
 * @author Jakub
 *
 */
public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener,  java.awt.event.KeyListener {
	/**
	 * some java stuff to get rid of warnings
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * image to be tagged
	 */
	BufferedImage image = null;
	
	/**
	 * list of current polygon's vertices 
	 */
	Polygon currentPolygon = null;
	
  /**
   * Index of polygonList indicating which polygon is selected
   *
   * -1 means that nothing is selected.
   */
	int selectedIndex = -1;
	
	/**
	 * list of polygons
	 */
	ArrayList<Polygon> polygonsList = null;
	
	/**
	 * Indicates whether we are snapping towards the first segment of a polygon.
	 */
	boolean snapping = false;
	/**
	 * Used for double buffering the rendering, only needed on DICE (I think). Causes some artifacts
	 */
	private BufferedImage offImg;
	private boolean newBufferedImage;
	private int w, h, m_x, m_y; // various coordinates
	/**
	 * Path to the currently edited image
	 */
	private String imageName;
	/**
	 * Index of point that is being currently dragged.
	 * If -1 then none is being dragged.
	 */
	private int pointBeingDragged = -1;
	/**
	 * Point where Drag operation began. Used for computing translations.
	 */
	private Point mousePressedPoint;
	/**
	 * list of Colors assigned in this order.
	 */
	Color[] colors = new Color[]{Color.RED, Color.BLACK, Color.BLUE, Color.CYAN, Color.GRAY, Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.DARK_GRAY, Color.PINK, Color.WHITE, Color.YELLOW};
	
	
	/**
	 * default constructor, sets up the window properties
	 */
	public ImagePanel() {
		currentPolygon = new Polygon();
		polygonsList = new ArrayList<Polygon>();
		this.setVisible(true);
		Dimension panelSize = new Dimension(800, 600);
		this.setSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setMaximumSize(panelSize);
		setToolTipText("Test"); // Overriden later to something more sensible
		setBorder(null);
		addMouseListener(this);
		addMouseMotionListener(this);
	  addKeyListener(this);
	}
	
	@Override
	public boolean isFocusable() { return true; } // enables keyboard input
	
	
	/**
	 * extended constructor - loads image to be labelled
	 * @param imageName - path to image
	 * @throws Exception if error loading the image
	 */
	public ImagePanel(String imageName) throws Exception{
		this();
		this.imageName = imageName;
		image = ImageIO.read(new File(imageName));
		if (image.getWidth() > 800 || image.getHeight() > 600) {
			int newWidth = image.getWidth() > 800 ? 800 : (image.getWidth() * 600)/image.getHeight();
			int newHeight = image.getHeight() > 600 ? 600 : (image.getHeight() * 800)/image.getWidth();
			System.out.println("SCALING TO " + newWidth + "x" + newHeight );
			Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
			image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(scaledImage, 0, 0, this);
			w = newWidth;
			h = newHeight;
			//updateUI();
		}
		if ((new File(imageName + ".labels")).exists()) { // Autoload labels file
		  load();
		}
		updateUI();
	}

  /**
	 * Used for double buffering the rendering, only needed on DICE (I think). Causes some artifacts
	 */
   public Graphics2D getBuffer() {
      Graphics2D g2 = null;
   
      if ( offImg == null || offImg.getWidth() != w ||
           offImg.getHeight() != h ) {
          offImg = (BufferedImage) createImage(w, h);
          newBufferedImage = true;
      }
   
      if ( offImg != null ) {
        g2 = offImg.createGraphics();
        g2.setBackground(getBackground());
      }
   
      // .. set attributes ..
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                          RenderingHints.VALUE_RENDER_QUALITY);
   
      // .. clear canvas ..
      g2.clearRect(0, 0, w, h);
   
      return g2;
    }
  
  /**
   * Autosave
   */
  public void save() {
    saveAs(imageName+".labels");
  }
  
  public void saveAs(String path) {
    try {
			FileWriter outFile = new FileWriter(path);
		  PrintWriter out = new PrintWriter(outFile);
      for (int i = 0; i < polygonsList.size(); i++) {
        out.println(polygonsList.get(i));
      }
      out.close();
    } catch (IOException e){
      e.printStackTrace();
    }
  }
  
  public void load() throws java.io.FileNotFoundException {
    File f = new File(imageName + ".labels");
    java.util.Scanner s = new java.util.Scanner(f);
    s.useDelimiter("\\n\\s*\\n");
    while(s.hasNext()) {
      String str = s.next();
      this.polygonsList.add(new Polygon(str));
    }
  }
  
	/**
	 * Displays the image
	 */
	public void ShowImage(Graphics2D g) {		
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}
	
	// Drawing code ahead. Prefferably I'd like to somehow move a lot of this to Polygon, but the pathIterator api is pretty wierd
	
	/**
	 * Draws a complete polygon with fill and label.
	 */
	public void drawComplete(Graphics2D g, Polygon p, Color c) {
	  g.setColor(c);
	  g.draw(p);
	  Point center = p.getCenter();
	  g.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 16));
	  g.drawString(p.getLabel(), center.getX() - p.getLabel().length() * 2, center.getY() + 8);
	  g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 40));
		g.fill(p);
	}
	
	/**
	 * Draws the highlighted points of a selected polygon.
	 */
	public void drawSelection(Graphics2D g, Polygon p, Color c) {
	  g.setColor(c);
	  ArrayList<Point> points = p.getPoints();
		for(int i = 0; i < points.size(); i++) {
			Point currentVertex = points.get(i);
			g.fillOval(currentVertex.getX() - 5, currentVertex.getY() - 5, 10, 10);
		}
	}
	
	/**
	 * Main painting method
	 */
	@Override
	public void paint(Graphics g) {
	  
	  w = getWidth(); 
    h = getHeight(); 

    if ( w <= 0 || h <= 0 )
        return;

    Graphics2D g2 = (Graphics2D)getBuffer();//getGraphics();
		//super.paint(g2);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                           RenderingHints.VALUE_RENDER_QUALITY);
    
       // .. clear canvas ..
      g2.clearRect(0, 0, w, h);
		//display image
		ShowImage(g2);
		int current_color = 1;
		//display all the completed polygons
		for(int i = 0; i < polygonsList.size(); i++) {
		  Polygon polygon = polygonsList.get(i);
		  Color color = colors[i];
			drawComplete(g2, polygon, color);
			current_color++;
		}
		if(selectedIndex != -1) {
		  drawSelection(g2, polygonsList.get(selectedIndex), colors[selectedIndex]);
		}
		//display current polygon
		if(currentPolygon != null)
		{
  		drawPolygon(g2, currentPolygon, colors[current_color - 1]);
      Point currentVertex = currentPolygon.last();
      if(currentVertex != null && !snapping) {
		    g2.drawLine(currentVertex.getX(), currentVertex.getY(), m_x, m_y);
		  } else if(currentVertex != null) {
		    Point firstVertex = currentPolygon.get(0);
		    g2.drawLine(currentVertex.getX(), currentVertex.getY(), firstVertex.getX(), firstVertex.getY());
		  }
  	}
		g2.dispose();

    if ( offImg != null && isShowing() ) {
        g.drawImage(offImg, 0, 0, this);
    }
    newBufferedImage = false;
	}
	
	/**
	 * displays a polygon without last stroke
	 * @param polygon to be displayed
	 */
	public void drawPolygon(Graphics2D g, Polygon polygon, Color color) {
		g.setColor(color);
		ArrayList<Point> points = polygon.getPoints();
		for(int i = 0; i < points.size(); i++) {
			Point currentVertex = points.get(i);
			if (i != 0) {
				Point prevVertex = points.get(i - 1);
				g.drawLine(prevVertex.getX(), prevVertex.getY(), currentVertex.getX(), currentVertex.getY());
			}
			g.fillOval(currentVertex.getX() - 5, currentVertex.getY() - 5, 10, 10);
		}
	}

	public String showTextField() {
	  String name = JOptionPane.showInputDialog("Please enter a label for the object: ");
	  if(name != null) {
      return name;
    } else {
      return "Unlabeled";
    }
	}
	
	public String showTextField(String value) {
	  String name = JOptionPane.showInputDialog("Please edit the object's label: ", value);
    if(name != null) {
      return name;
    } else {
      return value;
    }
    
	}
	
  /**
   * Mouse click handler
   */
	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		requestFocusInWindow();
		//check if the cursos withing image area
		if (x > image.getWidth() || y > image.getHeight()) {
			//if not do nothing
			return;
		}
		
		Graphics2D g = (Graphics2D)this.getGraphics();
		
		//if the left button than we will add a vertex to poly
		if (e.getButton() == MouseEvent.BUTTON1) {
			if(snapping) {
			  currentPolygon.setLabel(showTextField());
        polygonsList.add(currentPolygon);
        currentPolygon = new Polygon();
			} else if(currentPolygon.size() == 0 && e.getClickCount() == 1) {
			  // find the current image 
			  int li = selectedIndex;
			  selectedIndex = -1;
			  for (int i = 0; i < polygonsList.size(); i++) {
			   if(polygonsList.get(i).contains(x,y)) {
			     selectedIndex = i;
			     break;
			   }
			  }
			  if(selectedIndex == -1 && li == -1) {
			    currentPolygon.add(new Point(x,y));
			  }
			} else if(currentPolygon.size() == 0 && e.getClickCount() == 2 && selectedIndex != -1) {
			    Polygon p = polygonsList.get(selectedIndex);
			    p.setLabel(showTextField(p.getLabel()));
			} else if(selectedIndex == -1) {
			  currentPolygon.add(new Point(x,y));
			} else {
			  selectedIndex = -1;
			}
		}
    //paint(g);
    updateUI();
	}
	
	/**
	 * Mouse move handler, mainly changes the Cursor type to reflect action to be taken.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
	  m_x = e.getX();
		m_y = e.getY();
		
		if(currentPolygon != null && currentPolygon.size() > 2 && currentPolygon.closeToBeggening(m_x, m_y)) {
		  this.snapping = true;
		  this.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
		} else if(selectedIndex != -1 && polygonsList.get(selectedIndex).closeToCorner(m_x, m_y) != -1) {
		  this.setCursor(new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR));
		  this.snapping = false;
		} else if(selectedIndex != -1 && polygonsList.get(selectedIndex).contains(m_x, m_y)) {
		  this.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		  this.snapping = false;
		} else if(aboveAPolygon(m_x, m_y) || selectedIndex != -1) {
		  this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR ));
		  this.snapping = false;
		} else {
		  this.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
		  this.snapping = false;
		}
		
		if(currentPolygon != null)
		{
		  Graphics2D g = (Graphics2D)this.getGraphics();
		  paint(g);
		  //updateUI();
		  //revalidate();
		  //repaint(0, 0,0, getWidth(), getHeight());
		}
	}
	
	/**
	 * Returns true if (x,y) is inside any polygon.
	 */
	private boolean aboveAPolygon(int x, int y)
	{
	  for(Polygon p : polygonsList) {
	    if(p.contains(x,y)) {
	      return true;
	    }
	  }
	  return false;
	}
	
	/**
	 * Context sensitive help.
	 */
	@Override
	public String getToolTipText(MouseEvent e) {
	  m_x = e.getX();
		m_y = e.getY();
		if(currentPolygon != null && currentPolygon.size() > 2 && currentPolygon.closeToBeggening(m_x, m_y)) {
		  return "Click to complete current polygon.";
		} else if(selectedIndex != -1 && polygonsList.get(selectedIndex).closeToCorner(m_x, m_y) != -1) {
		  return "Drag to change corner position.";
		} else if(selectedIndex != -1 && polygonsList.get(selectedIndex).contains(m_x, m_y)) {
		  return "Drag to move polygon, double click to change label.";
		} else if(aboveAPolygon(m_x, m_y)) {
		  return "Click to select this polygon.";
		} else if(selectedIndex != -1) {
		  return "Click to deselect.";
		} else {
		  return "Click to add vertex.";
		}
	}
	
	/**
	 * Mouse drag handler, handles mostly editing.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
	  int i;
	  int x = e.getX(); int y = e.getY();
	  if(selectedIndex != -1) {
	    Polygon p = polygonsList.get(selectedIndex);
	    if(pointBeingDragged != -1) {
	      p.setPoint(pointBeingDragged, x, y);
	    } else if((i = p.closeToCorner(x, y)) != -1) {
  	    p.setPoint(i, x, y);
        pointBeingDragged = i;
      } else if(p.contains(x, y)) {
        p.translate(x - mousePressedPoint.getX(),y - mousePressedPoint.getY());
        mousePressedPoint = new Point(e.getX(), e.getY());
  	  } else {  	      
	      pointBeingDragged = -1;
	      return;
	    }
	    
	    //Graphics2D g = (Graphics2D)this.getGraphics();
		  //paint(g);
		  updateUI();
	  }
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	  mousePressedPoint = new Point(e.getX(), e.getY()); // used in mouse drag
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	  pointBeingDragged = -1;
	}
	

  public void keyTyped(KeyEvent e) {
  }

  /**
   * Nudge support - when user uses the arrows the currently selected object moves.
   */
  public void keyPressed(KeyEvent e) {
    switch(e.getKeyCode()) {    
      case KeyEvent.VK_LEFT:
  		  if(selectedIndex != -1) {
           polygonsList.get(selectedIndex).translate(-1, 0);
        }
        updateUI();
        break;
      case KeyEvent.VK_RIGHT:
  		  if(selectedIndex != -1) {
           polygonsList.get(selectedIndex).translate(1, 0);
        }
        updateUI();
        break;
      case KeyEvent.VK_UP:
  		  if(selectedIndex != -1) {
           polygonsList.get(selectedIndex).translate(0, -1);
        }
        updateUI();
        break;
      case KeyEvent.VK_DOWN:
  		  if(selectedIndex != -1) {
           polygonsList.get(selectedIndex).translate(0, 1);
        }
        updateUI();
        break;
    }
  }

  /**
   * Back space and delete delete the current object
   * Enter either deselects or completes current object
   */
  public void keyReleased(KeyEvent e) {
    System.out.println(e.getKeyCode());
    switch(e.getKeyCode()) {
      case KeyEvent.VK_BACK_SPACE:
      case KeyEvent.VK_DELETE:
        if(selectedIndex != -1) {
          polygonsList.remove(selectedIndex);
          selectedIndex = -1;
        } else {
          currentPolygon = new Polygon();
        }
        
  		  break;
  		case KeyEvent.VK_ENTER:
  		  if(currentPolygon != null && selectedIndex == -1 && currentPolygon.size() > 2) {
  		    currentPolygon.setLabel(showTextField());
          polygonsList.add(currentPolygon);
          currentPolygon = new Polygon();
  		  } else {
  		    selectedIndex = -1;
  		  }
  		  break;
  		
  		
    }
    //Graphics2D g = (Graphics2D)this.getGraphics();
	  //paint(g);
    updateUI();
  }
  
	
}
