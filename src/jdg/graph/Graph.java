package jdg.graph;

import java.util.*;

/**
 * Define the abstract data type for a graph (undirected, not weighted)
 * (explicit pointer-based representation of a graph)
 *
 * @author Luca Castelli Aleardi, Ecole Polytechnique (INF562)
 * @version dec 2012
 */
public interface Graph {
	
	/**
	 * Return a node given its index (vertices are stored in an arraylist)
	 */
	public Node getNode(int index);

	/**
	 * Return a node given its label (stored in an hash map)	 
	 */
	public Node getNode(String label);

    public void addEdge(Node d, Node a);
    public void removeEdge(Node d, Node a);
    public void addNode(Node v);
    public void removeNode(Node v);
    
    public boolean adjacent(Node d, Node a);
    public int degree(Node v);
    public Collection<Node> getNeighbors(Node v);
    
    public int sizeVertices();
    public String info();
}
