package jdg.layout;

import java.util.Random;

import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;

import Jcg.geometry.Point_3;

/**
 * Network visualization: abstract implementation of the force-directed paradigm
 * 
 * @author Luca Castelli Aleardi, Ecole Polytechnique (INF421)
 * @version october 2017
 */
public abstract class Layout {
	public AdjacencyListGraph g;
	public double w, h; // dimensions of the drawing area
	
	public static int seed=10;
	/** Random generator */	
	static Random generator = new Random(seed); // initialize random generator
	
	/**
	 * Initialize vertex locations at random (in a square of given size WxH)
	 */	
	public static void setRandomPoints(AdjacencyListGraph g, double w, double h) {
		Point_3 p;
		double w1=w/2., h1=h/2.;
		for(Node u: g.vertices){
			double n1=w1-2*w1*generator.nextDouble();
			double n2=h1-2*h1*generator.nextDouble();
		    p = new Point_3 (n1, n2, 0.0);
		    u.setPoint(p);
		}		
	}
	
	/**
	 * Perform one iteration of the Force-Directed algorithm.
	 * Positions of vertices are updated according to their
	 * mutual attractive and repulsive forces.
	 */	
	public abstract void computeLayout();
			
}
