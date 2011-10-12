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

import hci.utils.*;

/**
 * Handles image editing panel
 * @author Michal
 *
 */
public class ImagePanel extends JPanel implements MouseListener, MouseMotionListener {
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
	 * list of polygons
	 */
	ArrayList<Polygon> polygonsList = null;
	
	boolean snapping = false;
	
	private BufferedImage offImg;
	private int w, h, m_x, m_y;
	private boolean newBufferedImage;
	
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
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	/**
	 * extended constructor - loads image to be labelled
	 * @param imageName - path to image
	 * @throws Exception if error loading the image
	 */
	public ImagePanel(String imageName) throws Exception{
		this();
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
		}
	}

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
	 * Displays the image
	 */
	public void ShowImage(Graphics2D g) {		
		if (image != null) {
			g.drawImage(
					image, 0, 0, null);
		}
	}
	
	public void drawComplete(Graphics2D g, Polygon p, Color c) {
	  g.setColor(c);
	  g.draw(p);
	  g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 20));
		g.fill(p);
	}
	
	@Override
	public void paint(Graphics g) {
	  
	  w = getWidth(); 
    h = getHeight(); 

    if ( w <= 0 || h <= 0 )
        return;

    Graphics2D g2 = getBuffer();

    
	  
		super.paint(g2);
		
		//display iamge
		ShowImage(g2);
		int current_color = 1;
		//display all the completed polygons
		for(int i = 0; i < polygonsList.size(); i++) {

		  Polygon polygon = polygonsList.get(i);
		  Color color = colors[i];
			drawComplete(g2, polygon, color);
			current_color++;
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
	
	/**
	 * moves current polygon to the list of polygons and makes pace for a new one
	 */
	// public void addNewPolygon() {
	//     //finish the current polygon if any
	//     Graphics2D g = (Graphics2D)this.getGraphics();
	//     if (currentPolygon != null ) {
	//       //finishPolygon(g, currentPolygon, colors[current_color]);
	//       polygonsList.add(currentPolygon);
	//     }
	//     //current_color += 1;
	//     currentPolygon = new Polygon();
	//     paint(g);
	//   }

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		//check if the cursos withing image area
		if (x > image.getWidth() || y > image.getHeight()) {
			//if not do nothing
			return;
		}
		
		Graphics2D g = (Graphics2D)this.getGraphics();
		
		//if the left button than we will add a vertex to poly
		if (e.getButton() == MouseEvent.BUTTON1) {
		  // g.setColor(colors[current_color]);
		  //    if (currentPolygon.size() != 0) {
		  //      Point lastVertex = currentPolygon.get(currentPolygon.size() - 1);
		  //      g.drawLine(lastVertex.getX(), lastVertex.getY(), x, y);
		  //    }
		  //    g.fillOval(x-5,y-5,10,10);
			if(snapping) {
        polygonsList.add(currentPolygon);
        currentPolygon = new Polygon();
			} else {
			  currentPolygon.add(new Point(x,y));
			}
			
			System.out.println(x + " " + y);
		}
    paint(g);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
	  m_x = e.getX();
		m_y = e.getY();
		if(currentPolygon != null && currentPolygon.size() > 2 && currentPolygon.closeToBeggening(m_x, m_y)) {
		  this.snapping = true;
		} else {
		  this.snapping = false;
		}
		if(currentPolygon != null)
		{
		  Graphics2D g = (Graphics2D)this.getGraphics();
		  paint(g);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
	
}
