package jdg.graph;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import Jcg.geometry.Point_3;

/**
 * A class for representing a node of a graph
 *
 * @author Luca Castelli Aleardi
 */
public class Node {
	public Point_3 p; // geometric coordinates
	public ArrayList<Node> neighbors=null;
	String label; // label of the node: vertex label can differ from vertex index
	public int tag;
	public double weight=1;
	public int index; // an index from 0..n-1
	public Color color;
	public Node descedant=null; // descendant node in the coarsening process

	    public Node(int index) { 
	    	this.neighbors=new ArrayList<Node>();
	    	this.p=new Point_3();
	    	this.index=index;
	    	this.label=null; // no label defined
	    }
	    
	    public Node(int index, Point_3 p, Color color) { 
	    	this.neighbors=new ArrayList<Node>();
	    	this.index=index;
	    	this.p=p;
	    	this.color=color;
	    	this.label=null; // no label defined
	    }

	    public Node(int index, Point_3 p, Color color, String label) { 
	    	this.neighbors=new ArrayList<Node>();
	    	this.index=index;
	    	this.p=p;
	    	this.color=color;
	    	this.label=label;
	    }

	    public void addNeighbor(Node v) {
	    	if(v!=this && this.neighbors.contains(v)==false)
	    		this.neighbors.add(v);
	    }

	    public void removeNeighbor(Node v) {
	    	this.neighbors.remove(v);
	    }

	    public boolean adjacent(Node v) {
	    	return this.neighbors.contains(v);
	    }
	    
	    public boolean isIsolated() {
	    	if(this.neighbors==null || this.neighbors.size()==0)
	    		return true;
	    	return false;
	    }
	    
	    public void setColor(int r, int g, int b) {
	    	this.color=new Color(r, g, b);
	    }
	    
	    public List<Node> neighborsList() {
	    	return this.neighbors;
	    }
	    
	    public void setTag(int tag) { 
	    	this.tag=tag;
	    }  
	    
	    public int getTag() { 
	    	return this.tag; 
	    }
	    
	    public Point_3 getPoint() { 
	    	return this.p; 
	    }

	    public Point_3 setPoint(Point_3 q) { 
	    	return this.p=q; 
	    }

	    public void setLabel(String label) {
	    	this.label=label;
	    }
	    
	    public String getLabel() {
	    	return this.label;
	    }

	    public int degree() {
	    	return this.neighbors.size();
	    }
	    
	    public String toString(){
	        return "v"+tag;
	    }
	    
	    public int hashCode() {
	    	return this.index;
	    }

}
