package jdg.clustering;

import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;

/**
 * This class provides an implementatino of the Louvain algorithm for Community detection
 * 
 * @author Luca Castelli Aleardi (INF421, 2017)
 */
public class LouvainAlgorithm extends CommunityDetection {

	/**
	 * Initialize the parameters of the Louvain algorithm
	 */
	public LouvainAlgorithm() {
		// TO DO
	}
	
	/**
	 * This method returns a partition of a network of size 'n' into communities,
	 * computed by the Louvain algorithm <p>
	 * <p>
	 * Remarks:<p>
	 * -) the nodes of the networks are numbered 0..n-1
	 * -) the graph is partitioned into 'k' communities, which have numbers 0..k-1
	 * 
	 * @param graph  the input network (adjacency list representation)
	 * @return an array of size 'n' storing, for each vertex, the index of its community (a value between 0..,k-1)
	 */
	public int[] computeClusters(AdjacencyListGraph graph) {
		int n=graph.sizeVertices();
		int[] communities=new int[n];
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return communities;
		
		
	}

}
