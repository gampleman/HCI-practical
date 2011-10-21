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
 *
 */
public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener, java.awt.event.ActionListener, java.awt.event.KeyListener {
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
	
	int mode = 0;
	int selectedIndex = -1;
	
	/**
	 * list of polygons
	 */
	ArrayList<Polygon> polygonsList = null;
	
	boolean snapping = false;
	
	//private BufferedImage offImg;
	private int w, h, m_x, m_y;
	//private boolean newBufferedImage;
	private String imageName;
	private int pointBeingDragged = -1;
	private Point mousePressedPoint;
	private JLabel hint;
	/**
	 * list of Colors
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
		
		//hint = new JLabel("Test string");
		//add(hint);
		//System.out.println(isDoubleBuffered());
		setToolTipText("Test");
		setBorder(null);
		addMouseListener(this);
		addMouseMotionListener(this);
	  addKeyListener(this);
	}
	
	@Override
	public boolean isFocusable() { return true; }
	
	public void delete() {
	  System.out.println("Delete called");
	}
	
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
		if ((new File(imageName + ".labels")).exists()) {
		  System.out.println("Exists");
		  load();
		} else {
		  System.out.println("Exists not: " + imageName + ".labels");
		}
		updateUI();
	}

  // public Graphics2D getBuffer() {
  //    Graphics2D g2 = null;
  // 
  //    if ( offImg == null || offImg.getWidth() != w ||
  //         offImg.getHeight() != h ) {
  //        offImg = (BufferedImage) createImage(w, h);
  //        newBufferedImage = true;
  //    }
  // 
  //    if ( offImg != null ) {
  //        g2 = offImg.createGraphics();
  //        g2.setBackground(getBackground());
  //    }
  // 
  //    // .. set attributes ..
  //    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
  //                        RenderingHints.VALUE_ANTIALIAS_ON);
  //    g2.setRenderingHint(RenderingHints.KEY_RENDERING,
  //                        RenderingHints.VALUE_RENDER_QUALITY);
  // 
  //    // .. clear canvas ..
  //    g2.clearRect(0, 0, w, h);
  // 
  //    return g2;
  //  }
  
  public void save() {
    try {
			FileWriter outFile = new FileWriter(imageName+".labels");
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
    System.out.println("Loading");
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
	
	public void drawComplete(Graphics2D g, Polygon p, Color c) {
	  g.setColor(c);
	  g.draw(p);
	  Point center = p.getCenter();
	  g.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 16));
	  g.drawString(p.getLabel(), center.getX() - p.getLabel().length() * 2, center.getY() + 8);
	  g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 40));
		g.fill(p);
	}
	
	
	public void drawSelection(Graphics2D g, Polygon p, Color c) {
	  g.setColor(c);
	  ArrayList<Point> points = p.getPoints();
		for(int i = 0; i < points.size(); i++) {
			Point currentVertex = points.get(i);
			g.fillOval(currentVertex.getX() - 5, currentVertex.getY() - 5, 10, 10);
		}
	}
	
	@Override
	public void paint(Graphics g) {
	  
	  w = getWidth(); 
    h = getHeight(); 

    if ( w <= 0 || h <= 0 )
        return;

    Graphics2D g2 = (Graphics2D)getGraphics();//getBuffer();
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
		//g2.dispose();

    //if ( offImg != null && isShowing() ) {
    //    g.drawImage(offImg, 0, 0, this);
    //}
    //newBufferedImage = false;
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
	
	/**
	 * displays last stroke of the polygon (arch between the last and first vertices)
	 * @param polygon to be finished
	 */
	public void finishPolygon(Graphics2D g, Polygon polygon, Color color) {
		//if there are less than 3 vertices than nothing to be completed
		ArrayList<Point> points = polygon.getPoints();
		if (polygon.size() >= 3) {
			Point firstVertex = points.get(0);
			Point lastVertex = points.get(points.size() - 1);
			g.setColor(color);
			g.drawLine(firstVertex.getX(), firstVertex.getY(), lastVertex.getX(), lastVertex.getY());
			
			g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			g.fill(polygon);
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
	
	
	public void actionPerformed(java.awt.event.ActionEvent evt) {
      //String text = textField.getText();
      
  }


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
			  selectedIndex = -1;
			  for (int i = 0; i < polygonsList.size(); i++) {
			   if(polygonsList.get(i).contains(x,y)) {
			     selectedIndex = i;
			     break;
			   }
			  }
			  if(selectedIndex == -1) {
			    currentPolygon.add(new Point(x,y));
			  }
			} else if(currentPolygon.size() == 0 && e.getClickCount() == 2 && selectedIndex != -1) {
			    Polygon p = polygonsList.get(selectedIndex);
			    p.setLabel(showTextField(p.getLabel()));
			} else {
			  currentPolygon.add(new Point(x,y));
			  selectedIndex = -1;
			}
		}
    //paint(g);
    updateUI();
	}
	
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
		} else if(aboveAPolygon(m_x, m_y)) {
		  this.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR ));
		  this.snapping = false;
		} else {
		  this.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
		  this.snapping = false;
		}
		
		if(currentPolygon != null)
		{
		  //Graphics2D g = (Graphics2D)this.getGraphics();
		  //paint(g);
		  updateUI();
		  //revalidate();
		  //repaint(0, 0,0, getWidth(), getHeight());
		}
	}
	
	private boolean aboveAPolygon(int x, int y)
	{
	  for(Polygon p : polygonsList) {
	    if(p.contains(x,y)) {
	      return true;
	    }
	  }
	  return false;
	}
	
	/*
	Context sensitive help.
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
		} else {
		  return "Click to add vertex.";
		}
	}
	
	
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
	  mousePressedPoint = new Point(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	  pointBeingDragged = -1;
	}
	

  public void keyTyped(KeyEvent e) {
  }


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
