package hci.utils;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.io.*;
/**
 * simple class for handling points
 * @author Jakub
 * @see http://download.oracle.com/javase/1.4.2/docs/api/java/awt/Polygon.html
 */
public class Polygon extends java.awt.Polygon { // Uses superclass for efficient storage and optimized drawing

  String label = "";
	
	
	public Polygon() {
	  super();
	}
	/**
	 * Kept compatibility with old API
	 */
	public Polygon(ArrayList<Point> points) {
		super();
		for (Point p : points) {
		  addPoint(p.getX(), p.getY());
		}
	}
	
	/**
	 * Initializes Polygon from string as produced by toString();
	 */
	public Polygon(String str) {
	  java.util.Scanner s = new java.util.Scanner(str);
	  this.label = s.nextLine();
	  int x = -1;
	  while(s.hasNextInt()) {
	    if(x != -1) {
	      addPoint(x, s.nextInt());
	      x = -1;
	    } else {
	      x = s.nextInt();
	    }
	  }
	}
	
	public Polygon(String label, ArrayList<Point> points) {
		this(points);
		this.label = label;
	}

	public ArrayList<Point> getPoints() {
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < npoints ; i++) {
		  points.add(new Point(xpoints[i], ypoints[i]));
		}
		return points;
	}

	public void setPoints(ArrayList<Point> points) {
		// TBD
		//this.points = points;
	}
  
  public void setPoint(int index, int x, int y) {
    xpoints[index] = x;
    ypoints[index] = y;
    invalidate(); // recalculates things like the bounds (so that label is repositioned)
  }

	public String getLabel() {
		return label;
	}

  
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Returns true if (x,y) is sqrt(125) pixels from the first point
	 */
	public boolean closeToBeggening(int x, int y) {
	  return d2(x,y, xpoints[0], ypoints[0]) < 125;
	}
	
	/**
	 * Returns index of point that (x,y) is closer then sqrt(145) pixels to.
	 * Returns -1 if none such exists.
	 */
	public int closeToCorner(int x, int y) {
	  for (int i = 0; i < npoints ; i++) {
		  if(d2(x,y,xpoints[i], ypoints[i]) < 145) {
		    return i;
		  }
		}
		return -1;
	}
	
	private int d2(int x, int y, int x1, int y1) { //= squared euclidian distance 
	  int dist_x = x - x1;
	  int dist_y = y - y1;
	  return dist_x * dist_x + dist_y * dist_y;
	}
	
	// Delegate methods to underlying points
	
	public void add(Point p) {
	  addPoint(p.getX(), p.getY());
	}
	
	public int size() {
	  return npoints;
	}
	
	public Point get(int n) {
	  return new Point(xpoints[n], ypoints[n]);
	}
	
	public Point getCenter() {
	  java.awt.Rectangle r = this.getBounds();
	  return new Point(r.getX()  + r.getWidth()/ 2, r.getY() + r.getHeight() / 2);
	}
	
	public Point last() {
	  Point p;
    try {
	     p = new Point(xpoints[npoints - 1], ypoints[npoints - 1]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      p = null;
    }
	  return p;
	}
	
	/**
	 * Used likewise for debugging as well as for saving.
	 */
	public String toString() {
	  String s = getLabel() + "\n";
	  for (int i=0; i<npoints; i++){
    	s += "  " + xpoints[i] + " " + ypoints[i] + "\n";
    }
    return s;
	}


}
