package jdg.graph;

import java.util.*;

import Jcg.geometry.PointCloud_3;
import Jcg.geometry.Point_3;

/**
 * Pointer based implementation of an Adjacency List Representation of a graph
 * 
 * @author Luca Castelli Aleardi
 *
 */
public class AdjacencyListGraph implements Graph {
	
	public ArrayList<Node> vertices; // list of vertices
	public HashMap<String,Node> labelMap; // map between vertices and their labels
	
	//private static final int initialCapacity=1000; // initial size of the hash map
	
	public AdjacencyListGraph() {
		this.vertices=new ArrayList<Node>();
		this.labelMap=null; // no labels defined
	}

	public AdjacencyListGraph(int mapCapacity) {
		this.vertices=new ArrayList<Node>();
		this.labelMap=new HashMap<String,Node>(mapCapacity); // labels are defined
	}
	
	public void addNode(Node v) {
		String label=v.label;
		if(label==null) {
			this.vertices.add(v);
			return;
		}
		
		if(this.labelMap.containsKey(label)==false) {
			this.labelMap.put(label, v);
			this.vertices.add(v);
		}
	}
	
	public Node getNode(String label) {
		if(this.labelMap!=null && this.labelMap.containsKey(label)==true) {
			return this.labelMap.get(label);
		}
		return null;
	}
	
	public Node getNode(int index) {
		if(index>=0 && index<this.vertices.size()) {
			return this.vertices.get(index);
		}
		return null;
	}
	
	public void removeNode(Node v) {
		throw new Error("To be updated/implemented");
/*		if(this.vertices.contains(v)==false)
			return;
		for(Node u: this.getNeighbors(v)) { // remove all edges between v and its neighbors
			u.removeNeighbor(v);
		}
		this.vertices.remove(v); // remove the vertex from the graph*/
	}

    public void addEdge(Node a, Node b) {
    	if(a==null || b==null)
    		return;
    	a.addNeighbor(b);
    	b.addNeighbor(a);
    }

    public void removeEdge(Node a, Node b){
    	if(a==null || b==null)
    		return;
    	a.removeNeighbor(b);
    	b.removeNeighbor(a);
    }
    
    public boolean adjacent(Node a, Node b) {
    	if(a==null || b==null)
    		throw new Error("Graph error: vertices not defined");
    	return a.adjacent(b);
    }
    
    public int degree(Node v) {
    	return v.degree();
    }
    
    public Collection<Node> getNeighbors(Node v) {
    	return v.neighborsList();
    }
        
	/**
     * Return the number of nodes
     */		
    public int sizeVertices() {
    	return this.vertices.size();
    }
    
	/**
     * Return the number of arcs
     * 
     * Remark: arcs are not counted twice
     */		
    public int sizeEdges() {
    	int result=0;
    	for(Node v: this.vertices)
    		result=result+getNeighbors(v).size();
    	return result/2;
    }
    
	/**
     * compute and return the bounding box for the points set.
     * The result is stored in a table.
     */		
    public Point_3[] boundingBox() {
    	PointCloud_3 points=new PointCloud_3(this.listOfPoints());
    	return points.boundingBox();
    }
	
    /**
     * Return the list of vertex points of the graph
     */		   
    public ArrayList<Point_3> listOfPoints() {
    	ArrayList<Point_3> result=new ArrayList<Point_3>();
    	for(Node u: this.vertices) {
    		if(u!=null && u.getPoint()!=null)
    			result.add(u.getPoint());
    	}
    	return result;
    }
    
    /**
     * Return an array storing all vertex indices, according to the order of vertices
     */		   
    public int[] getIndices() {
    	int[] result=new int[this.vertices.size()];
    	
    	int count=0;
    	for(Node u: this.vertices) {
    		if(u!=null) {
    			result[count]=u.index;
    			count++;
    		}
    	}
    	return result;
    }
    
    /**
     * Return an array storing all vertex locations, according to the order of vertices
     */		   
    public Point_3[] getPositions() {
    	Point_3[] result=new Point_3[this.vertices.size()];
    	
    	int count=0;
    	for(Node u: this.vertices) {
    		if(u!=null && u.getPoint()!=null) {
    			result[count]=u.getPoint();
    			count++;
    		}
    	}
    	return result;
    }
    
    /**
     * Compute the 2D bounding box
     */    
    public Point_3[] compute2DBoundingBox() {
    	double 	xmin=Double.MAX_VALUE, xmax=Double.MIN_VALUE, 
    			ymin=Double.MAX_VALUE, ymax=Double.MIN_VALUE;
    	
    	double x, y;
    	for(Node u: this.vertices) {
    		x=u.getPoint().getX().doubleValue();
    		y=u.getPoint().getY().doubleValue();
    		if (x<xmin)
    			xmin = x;
    		if (x>xmax)
    			xmax = x;
    		if (y<ymin)
    			ymin = y;
    		if (y>ymax)
    			ymax = y;
    	}
    	Point_3 p=new Point_3(xmin, ymin, 0.0);
    	Point_3 q=new Point_3(xmax, ymax, 0.0);
    	//System.out.println("\nBounding box: "+p+" - "+q);
    	return new Point_3[]{p, q};
    }
    
    /**
     * Compute and return the connected component containing the vertex v
     * It performs a DFS visit of the graph starting from vertex v
     * <p>
     * Remark: the graph is assumed to be undirected
     * 
     * @param v  the starting node
     * @return the list of nodes lying in the same connected component of v
     */		   
    public List<Node> findConnectedComponent(Node v){
    	if(v==null)
    		return null;
    	
    	LinkedList<Node> component=new LinkedList<Node>(); // the connected component containing v
    	
    	HashSet<Node> visited=new HashSet<Node>(); // the set of vertices already visited
    	LinkedList<Node> stack=new LinkedList<Node>(); // stack containing the node to visit
    	
    	stack.add(v);
    	while(stack.isEmpty()==false) {
    		Node u=stack.poll(); // get and removes the node in the head of the stack
    		if(visited.contains(u)==false) {
    			visited.add(u); // mark the vertex as visited
    			component.add(u); // add the vertex to the connected component
    			for(Node neighbor: u.neighbors)
    				stack.add(neighbor); // add all neighboring vertices to the stack
    		}
    	}
    	
    	return component;
    }
        
    /**
     * Check whether the graph is connected
     * 
     * Remark: the graph is assumed to be undirected
     * 
     */		   
    public boolean isConnected(){
    	int isolatedVertices=0;
    	for(Node v: this.vertices)
    		if(v==null || v.degree()==0)
    			isolatedVertices++; // count isolated vertices
    	if(isolatedVertices>0) // is there are isolated vertices the graph cannot be connected
    		return false;
    	
    	// compute the size of the connected component containing v0
    	int sizeConnectedComponent=this.findConnectedComponent(this.vertices.get(0)).size();
    	
    	if(sizeConnectedComponent==this.sizeVertices())
    		return true;
    	return false;
    }
    
    /**
     * Compute the minimum vertex index of the graph (a non negative number)
     * 
     * Remark: vertices are allowed to have indices between 0..infty
     * This is required when graphs are dynamic: vertices can be removed
     */		   
    public int minVertexIndex() {
    	int result=Integer.MAX_VALUE;
    	for(Node v: this.vertices) {
    		if(v!=null)
    			result=Math.min(result, v.index); // compute max degree
    	}
    	return result;
    }

    /**
     * Compute the maximum vertex index of the graph (a non negative number)
     * 
     * Remark: vertices are allowed to have indices between 0..infty
     * This is required when graphs are dynamic: vertices can be removed
     */		   
    public int maxVertexIndex() {
    	int result=0;
    	for(Node v: this.vertices) {
    		if(v!=null)
    			result=Math.max(result, v.index); // compute max degree
    	}
    	return result;
    }
    
    /**
     * Return a string containing informations and parameters of the graph
     */		   
    public String info() {
    	String result=sizeVertices()+" vertices, "+sizeEdges()+" edges\n";
    	
    	int isolatedVertices=0;
    	int maxDegree=0;
    	for(Node v: this.vertices) {
    		if(v==null || v.degree()==0)
    			isolatedVertices++; // count isolated vertices
    		//if(v!=null && v.p!=null && v.p.distanceFrom(new Point_3()).doubleValue()>0.) // check geometric coordinates
    		//	geoCoordinates=true;
    		if(v!=null)
    			maxDegree=Math.max(maxDegree, v.degree()); // compute max degree
    	}
    	result=result+"isolated vertices: "+isolatedVertices+"\n";
    	result=result+"max vertex degree: "+maxDegree+"\n";
    	
    	result=result+"min and max vertex index: "+minVertexIndex();
    	result=result+"..."+maxVertexIndex()+"\n";
    	
    	if(this.isConnected()==true)
    		result=result+"the graph is connected\n";
    	else
    		result=result+"the graph is not connected\n";
    	
    	return result;
    }
    
    /**
     * Remove all isolated vertices (having degree 0): vertex indices are recomputed and updated
     */		   
    public void removeIsolatedVertices() {
    	System.out.print("Removing isolated vertices...");
    	ArrayList<Node> oldVertices=this.vertices; // vertices in the original graph
    	
    	int count=0; // counter for isolated vertices
    	for(Node v: oldVertices) {
    		if(v==null || v.degree()==0) {
    			count++; // count isolated vertices
    		}
    	}
    	
    	this.vertices=new ArrayList<Node>(count); // re-initialize the array storing the vertices
    	this.labelMap=new HashMap<String,Node>(count); // re-initialize the hash map (for storing vertex labels)
    	
    	int i=0;
    	for(Node v: oldVertices) {
    		if(v!=null && v.degree()!=0) { // only add a vertex with degree >0
     			v.index=i; // update vertex index
     			this.addNode(v);
     			i++;
    		}
    	}
    }
    
    public void printCoordinates() {
    	for(Node v: this.vertices) {
    		if(v!=null)
    			System.out.println(v.index+" "+v.p);
    	}
    }

}
