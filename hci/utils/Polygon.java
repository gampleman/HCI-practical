package hci.utils;

import java.util.ArrayList;
//import java.awt.Polygon;

/**
 * simple class for handling points
 * @author Jakub
 * @see http://download.oracle.com/javase/1.4.2/docs/api/java/awt/Polygon.html
 */
public class Polygon extends java.awt.Polygon {

  String label = "";
  //ArrayList<Point> points = null;
	
	
	public Polygon() {
	  super();
	}
	
	public Polygon(ArrayList<Point> points) {
		super();
		for (Point p : points) {
		  addPoint(p.getX(), p.getY());
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean closeToBeggening(int x, int y) {
	  int dist_x = x - xpoints[0];
	  int dist_y = y - ypoints[0];
	  return dist_x * dist_x + dist_y * dist_y < 125;
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
	
	public Point last() {
	  Point p;
    try {
	     p = new Point(xpoints[npoints - 1], ypoints[npoints - 1]);
    } catch (java.lang.ArrayIndexOutOfBoundsException e) {
      p = null;
    }
	  return p;
	}
	
}
