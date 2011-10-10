package hci.utils;

import java.util.ArrayList;

/**
 * simple class for handling points
 * @author Jakub
 *
 */
public class Polygon {

  String label = "";
	ArrayList<Point> points = null;
	
	
	public Polygon() {
	  this.points = new ArrayList<Point>();
	}
	
	public Polygon(ArrayList<Point> points) {
		this.points = points;
	}
	
	public Polygon(String label, ArrayList<Point> points) {
		this.label = label;
		this.points = points;
	}

	public ArrayList<Point> getPoints() {
		return this.points;
	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	// Delegate methods to underlying points
	
	public void add(Point p) {
	  this.points.add(p);
	}
	
	public int size() {
	  return points.size();
	}
	
	public Point get(int n) {
	  return points.get(n);
	}
	
}
