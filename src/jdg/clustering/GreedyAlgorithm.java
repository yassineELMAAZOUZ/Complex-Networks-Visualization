package jdg.clustering;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;

/**
 * This class provides an implementation of the Greedy algorithm for Community
 * detection
 * 
 * @author Luca Castelli Aleardi (INF421, 2017)
 */
public class GreedyAlgorithm extends CommunityDetection {

	/**
	 * Initialize the parameters of the Louvain algorithm
	 */

	public GreedyAlgorithm() {

	}

	/**
	 * This method returns a partition of a network of size 'n' into communities,
	 * computed by the Louvain algorithm
	 * <p>
	 * <p>
	 * Remarks:
	 * <p>
	 * -) the nodes of the networks are numbered 0..n-1 -) the graph is partitioned
	 * into 'k' communities, which have numbers 0..k-1
	 * 
	 * @param graph
	 *            the input network (adjacency list representation)
	 * @return an array of size 'n' storing, for each vertex, the index of its
	 *         community (a value between 0..,k-1)
	 */

	public int[] computeClusters(AdjacencyListGraph g) {

		System.out.println("Computing clusters.....");
		
		int n = g.sizeVertices();
		int m = g.sizeEdges();

		HashMap<Integer, HashMap<Integer, Double>> E = new HashMap<Integer, HashMap<Integer, Double>>();

		HashMap<Integer, Double> A = new HashMap<Integer, Double>();

		HashMap<Integer, Set<Integer>> clusters = new HashMap<Integer, Set<Integer>>();

		HashMap<Integer, Set<Integer>> optimalClustering = (HashMap<Integer, Set<Integer>>) clusters.clone();

		// Setting up communities and clusters.
		int[] communities = new int[n];

		
		double currentModularity=0, maximalModularity=0;
		
		
		for (int k = 0; k < n; k++) {
			communities[k] = k;
			Set<Integer> set = new HashSet<Integer>();
			set.add(k);
			clusters.put(k, set);
		}

		// Setting up linkedTo, E and A values
		int N = g.sizeVertices();

		double a =0;
		
		for (int k = 0; k < N; k++) {
			HashMap<Integer, Double> map = new HashMap<Integer, Double>();
			
			for (int l = 0; l < N; l++) {
				
				if (g.getNode(k).neighbors.contains(g.getNode(l))) {
					map.put(l, 1. / m);
				} else {
					map.put(l, 0.);
				}

			}

			E.put(k, map);
			
			a = ((double) g.getNode(k).degree()) / m;
			currentModularity -= a;
					
			A.put(k,a);
			
		}

	
		maximalModularity = currentModularity;
		
		


		
		
		
		while(n>1) {
			
			//Searching for the optimal merge.
			
			double DQ = Double.NEGATIVE_INFINITY;
			
			double currentValue;
			
			int p=0,q=0;
			for(Integer i: E.keySet()) {
				for(Integer j: E.keySet()) {
					currentValue = 2.*(E.get(i).get(j) - A.get(i)*A.get(j)); 
					if(currentValue>DQ) {
						DQ = currentValue;
						p=i;q=j;
					}
				}
			}
			
			
			currentModularity +=DQ;
			
			
			//merging the communities p and q:
			
			      //Step 1: changing the values in E and A.
						
						A.put(p,A.get(p)+A.get(q));
						A.remove(q);
						
						for(Integer i: E.keySet()) {
							
							if(i!=p && i!=q) {
								E.get(p).put(i, E.get(p).get(i) + E.get(q).get(i));
								E.get(i).put(p,E.get(p).get(i));
							}
							
							E.get(i).remove(q);
							
						}
						
						E.get(p).remove(q);
						E.remove(q);
						
						
				  //Step 2: changing the values of clusters.
						
						clusters.get(p).addAll(clusters.get(q));
						clusters.remove(q);
						
						
						if(currentModularity>=maximalModularity) {
							
							maximalModularity = currentModularity;
							
							optimalClustering = (HashMap<Integer, Set<Integer>>) clusters.clone();
								
						}
						
			n--;
		
		}
		
		
		int k=0;
		
		for(Integer i: optimalClustering.keySet()) {
			
			for(Integer index: optimalClustering.get(i)) {
				
				communities[index] = k;
				
			}
			
			k++;
			
			
		}
		
		
		
		System.out.println("Done!");
	
		return communities;
	}

}
