package hci.utils;

import java.util.ArrayList;
//import java.awt.Polygon;

/**
 * simple class for handling points
 * @author Jakub
 *
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
	
}
