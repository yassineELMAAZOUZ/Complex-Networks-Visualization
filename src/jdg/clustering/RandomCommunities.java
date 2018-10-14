package jdg.clustering;

import java.util.Random;

import jdg.graph.AdjacencyListGraph;

/**
 * This class computes a random partition of the graph into 'k' communities.
 * 
 * @author Luca Castelli Aleardi (INF421, 2017)
 */
public class RandomCommunities extends CommunityDetection {

	int k;
	Random random = new Random();
	
	/**
	 * Initialize the number 'k' communities
	 */
	public RandomCommunities(int k) {
		this.k=k;
	}
	
	/**
	 * This method returns a random partition of a network of size 'n' into 'k' communities <p>
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
		
		if(k<0 || k>n)
			throw new Error("The parameter k="+k+" is wrong");
			
		int[] communities=new int[n];
		for(int i=0;i<n;i++)
			communities[i]=random.nextInt(k);
			
        return communities;
	}

}
