package jdg.graph;

import java.util.List;

/**
 * A small collection of graph algorithms
 * 
 * @author Luca Castelli Aleardi
 *
 */
public class GraphAlgorithms {

    /**
     * Given an input graph G, extract and return (a copy of) the induced sub-graph G', having a given sub-set of vertices
     * <p>
     * Remark: the graph G is assumed to be undirected
     * 
     * @param g  the input graph
     * @param vertexExists[]  an array (boolean) saying whether a given node must appear in the sub-graph G'
     * 
     * @return the (induced) sub-graph G'
     */		   
    public static AdjacencyListGraph extractSubGraph(AdjacencyListGraph g, boolean[] vertexExists){
    	System.out.print("Extracting subgraph...");
    	if(g==null || vertexExists==null)
    		return null;
    	if(g.sizeVertices()!=vertexExists.length)
    		throw new Error("Error: the number of vertices is wrong: "+g.sizeVertices()+" versus "+vertexExists.length);
    	
    	AdjacencyListGraph subgraph=new AdjacencyListGraph(); // the resulting sub-graph
    	
    	int vertexCount=0; // number of vertices of the sub-graph
    	int[] vertexPermutation=new int[vertexExists.length]; // array storing the index mapping between the input graph and the sub-graph
    	
    	// initialize the nodes
    	for(int i=0;i<vertexExists.length;i++) { 
    		if(vertexExists[i]==true) {
    			subgraph.addNode(new Node(vertexCount, g.getNode(i).getPoint(), null));
    			vertexPermutation[i]=vertexCount;
    			//System.out.println("v"+i+" ---> "+vertexPermutation[i]);
    			vertexCount++;
    		}
    		else
    			vertexPermutation[i]=-1; // the old node (in the original graph) will be removed
    	}
    	
    	// add edges to the sub-graph
    	//System.out.println("adding edges");
    	for(int i=0;i<vertexExists.length;i++) { 
    		if(vertexExists[i]==true) {
    			Node u=g.getNode(i); // node u in input graph G
    			Node uS=subgraph.getNode(vertexPermutation[i]); // corresponding node u' in the sub-graph G'
    			//System.out.print(""+i+": "+u.index+" ---> "+uS.index);
    			
    			for(Node v: u.neighbors) { // visit all neighbors v of u (in graph G)
    				if(vertexExists[v.index]==true) { // add edge (u', v') if both vertices exist in G'
    					Node vS=subgraph.getNode(vertexPermutation[v.index]); // vertex v' in G'
    					
    	    			if(uS!=vS && g.adjacent(uS, vS)==false && g.adjacent(vS, uS)==false) {
    	    				g.addEdge(uS, vS); // addEdge already adds the two edges (v1, v2) and (v2, v1)
    	    			}
    				}
    			}
    		}
    		
    	}
    	
    	System.out.println("done");
    	System.out.println(subgraph.info());
    	return subgraph;
    }

    /**
     * Given two input graphs G1, G2 and a vertex U, extract a sub-graph from g2 as defined below
     * <p>
     * -) first compute the connected component C1 containing U in G1
     * -) let S1 denote the vertex set of the component C1
     * -) extract from G2 the subgraph whose vertex set corresponds to S1
     * <p>
     * Remark: the graphs G1 and G2 are assumed to be undirected, and having the same number of nodes
     * 
     * @param g1  first input graph
     * @param g2  second input graph
     * @param indexU  index of a vertex in G1
     * 
     * @return the (induced) sub-graph G'
     */		   
    public static AdjacencyListGraph extractComponent(AdjacencyListGraph g1, int indexU, AdjacencyListGraph g2){
    	if(g1==null || g2==null || g1.sizeVertices()!=g2.sizeVertices())
    		throw new Error("Error: the number of vertices is wrong: "+g1.sizeVertices()+" versus "+g2.sizeVertices());
    	
    	Node u=g1.getNode(indexU); // node U in G1
    	List<Node> componentC1=g1.findConnectedComponent(u); // connected sub-graph C1 in G1
    	
    	boolean[] vertexExists=new boolean[g1.sizeVertices()];
    	for(int i=0;i<vertexExists.length;i++)
    		vertexExists[i]=false;
    	for(Node v: componentC1) {
    		vertexExists[v.index]=true; // set value TRUE, for those vertices which belong to C1
    		//System.out.println("extracted v"+v.index);
    	}
    	
    	AdjacencyListGraph result; // the result (sub-graph of G2)
    	result=extractSubGraph(g2, vertexExists);
    	
    	System.out.println("Extracted connected component of size "+componentC1.size());
    	return result;
    }

}
