package jdg.clustering;

import java.util.HashMap;
import java.util.LinkedList;

import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;

/**
 * This class provides methods for computing the modularity of a partition and
 * for solving the community detection problem
 * 
 * @author Luca Castelli Aleardi (INF421, 2017)
 */
public abstract class CommunityDetection {

	/**
	 * This method returns a partition of a network of size 'n' into communities. <p>
	 * <p>
	 * Remarks:<p>
	 * -) the nodes of the networks are numbered 0..n-1
	 * -) the graph is partitioned into 'k' communities, which have numbers 0..k-1
	 * 
	 * @param graph  the input network (adjacency list representation)
	 * @return an array of size 'n' storing, for each vertex, the index of its community (a value between 0..,k-1)
	 */
	public abstract int[] computeClusters(AdjacencyListGraph graph);
	
	/**
	 * This method computes the modularity of a partition of the input graph whose nodes
	 * are regrouped into 'k' communities. <p>
	 * <p>
	 * Remarks:<p>
	 * -) the nodes of the networks are numbered 0..n-1
	 * -) the graph is partitioned into 'k' communities, which have numbers 0..k-1
	 * 
	 * @param graph  the input network (adjacency list representation)
	 * @param communities  an array of size 'n' storing, for each vertex, the index of its community (a value between 0..,k-1)
	 * @return  the modularity of the partition
	 */
	public  double computeModularity(AdjacencyListGraph g, int[] communities) {
		double modularity=0;
		double L = g.sizeEdges();
		HashMap<Integer,LinkedList<Node>> map = new HashMap<Integer,LinkedList<Node>>();
		
		// Organising the nodes in partition sets.
		for(int k=0; k<communities.length; k++) {
			if(map.keySet().contains(communities[k])) {
				map.get(communities[k]).add(g.getNode(k));
			}
			else {
				LinkedList<Node> list = new LinkedList<>(); 
				list.add(g.getNode(k));
				map.put(communities[k],list);
			}
		}
		
		for(LinkedList<Node> list: map.values()) {
			
			double Kc=0,Lc=0;
			
			for(Node u: list) {
				for(Node v: u.neighbors) {	
					
					if(list.contains(v)) {
						Lc = Lc + 0.5;
					}
				}
				
				Kc = Kc + u.degree();
			}
			
			modularity = modularity + (Lc/L) - Math.pow( Kc/(2*L) , 2 );
		}
		
		return modularity;
	}
	
	
	
	/**
	 * This method returns the community of a vertex <p>
	 * <p>
	 * Remarks:<p>
	 * -) the graph is assumed to be endowed with a partition of its nodes into 'k' communities
	 * -) the nodes of the networks are numbered 0..n-1
	 * 
	 * @param graph  the input network (adjacency list representation)
	 * @param communities  an array of size 'n' storing, for each vertex, the index of its community (a value between 0..,k-1)
	 * @param u  a node in the network (whose index is a number between 0 and n-1)
	 * @return  the community to which node 'u' belongs: the result is a value between 0 and k-1
	 */
	public int getCommunity(AdjacencyListGraph graph, int[] communities, Node u) {
		if(u.index<0 || u.index>=graph.sizeVertices()) {
			throw new Error("Error: wrong vertex number v"+u.index);
		}
		return communities[u.index];
	}

}
