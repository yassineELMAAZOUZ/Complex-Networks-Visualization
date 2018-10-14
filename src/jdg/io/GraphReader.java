package jdg.io;

import jdg.graph.AdjacencyListGraph;

/**
 * Abstract class defining methods provided by a Graph Reader
 * 
 * @author Luca Castelli Aleardi
 *
 */
public abstract class GraphReader {
    
    /**
     * Construct the graph from an input file.
     */		   
    public abstract AdjacencyListGraph read(String inputGraph);
    
    /**
     * Read the geometric coordinates of nodes of a given graph g
     * 
     * @param g the input graph
     * @param inputData the file contained the vertex coordinates
     */		   
    public abstract void readGeometry(AdjacencyListGraph g, String inputData);
    
}
