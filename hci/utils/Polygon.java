package hci.utils;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import java.io.*;
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
    invalidate();
  }

	public String getLabel() {
		return label;
	}

  
	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean closeToBeggening(int x, int y) {
	  return d2(x,y, xpoints[0], ypoints[0]) < 125;
	}
	
	public int closeToCorner(int x, int y) {
	  for (int i = 0; i < npoints ; i++) {
		  if(d2(x,y,xpoints[i], ypoints[i]) < 145) {
		    return i;
		  }
		}
		return -1;
	}
	
	private int d2(int x, int y, int x1, int y1) { // distance squared
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
	
	public String toString() {
	  String s = getLabel() + "\n";
	  for (int i=0; i<npoints; i++){
    	s += "  " + xpoints[i] + " " + ypoints[i] + "\n";
    }
    return s;
	}

//save each object to a particular file (name of the file is the name of the object)
	public void write2File(String label, ArrayList<Point> points){
		try {
			FileWriter outFile = new FileWriter(label+".txt");
		    PrintWriter out = new PrintWriter(outFile);
            for (int i=0; i<points.size(); i++){
            	out.println(points.get(i).getX() +" "+points.get(i).getY());
            }
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
	}
	
	//input object's name
	public String setLabel(int unnamed){//number of unnamed objects
 		String name = JOptionPane.showInputDialog(
                 null, "Please enter the object name: ");
 		if(name != null && name.length() > 0){//valid input
 			JOptionPane.showMessageDialog(null, "Thank you!", "HCI", 1);
 		}else{//inputed nothing or cancelled
 			JOptionPane.showMessageDialog(null, "The program will choose a name for this object", "HCI", 1);
 			name = "unnamed"+Integer.toString(unnamed);
 		}
 		return name;
  	   }
	
}
