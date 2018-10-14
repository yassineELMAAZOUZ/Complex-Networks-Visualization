package jdg.layout;

import Jcg.geometry.Point_3;
import Jcg.geometry.Vector_3;
import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;
import jdg.graph.Octree;

/**
 * A class implementing the Fruchterman and Reingold method with fast
 * approximatino of repulsive forces (using octrees)
 * 
 * @author Luca Castelli Aleardi, Ecole Polytechnique
 * @version fev 2017
 */
public class FastFR91Layout extends Layout {
	// parameters of the algorithm by Fruchterman and Reingold
	public double k; // natural spring length
	public double area; // area of the drawing (width times height)
	public double C; // step
	public double temperature; // initial temperature
	public double minTemperature; // minimal temperature (strictly positive)
	public double coolingConstant; // constant term: the temperature decreases linearly at each iteration
	public boolean useCooling; // say whether performing simulated annealing

	public int iterationCount = 0; // count the number of performed iterations
	private int countRepulsive = 0; // count the number of computed repulsive forces (to measure time performances)

	/**
	 * Initialize the parameters of the force-directed layout
	 * 
	 * @param g
	 *            input graph to draw
	 * @param w
	 *            width of the drawing area
	 * @param h
	 *            height of the drawing area
	 * @param C
	 *            step length
	 */
	public FastFR91Layout(AdjacencyListGraph g, double w, double h) {
		System.out.print("Initializing force-directed method: fast Fruchterman-Reingold 91...");
		if(g==null) {
			System.out.println("Input graph not defined");
			System.exit(0);
		}
		this.g=g;
		int N=g.sizeVertices();
		
		// set the parameters of the algorithm FR91
		this.C=1.;
		this.w=w;
		this.h=h;
		this.area=w*h;
		this.k=C*Math.sqrt(area/N);
		this.temperature=w/5.; // the temperature is a fraction of the width of the drawing area
		this.minTemperature=0.05;
		this.coolingConstant=0.99;
		
		System.out.println("done ("+N+" nodes)");
		//System.out.println("k="+k+" - temperature="+temperature);
		System.out.println(this.toString());
	}

	public double repulsiveForce(double distance) {
		countRepulsive++;
		return (k * k) / distance;
	}

	public double attractiveForce(double distance) {
		return (distance * distance) / k;
	}

	public Vector_3 computeAttractiveForce(Node u) {
		Vector_3 disp = new Vector_3(0, 0, 0);
		Vector_3 delta = new Vector_3(0, 0, 0);
		double distance;

		for (Node v : u.neighbors) {
			delta = (Vector_3) u.getPoint().minus(v.getPoint());
			distance = Math.sqrt((double) delta.squaredLength());
			delta = delta.divisionByScalar(distance);

			delta = delta.multiplyByScalar(this.attractiveForce(distance));
			disp = disp.sum(delta);
		}

		return disp;
	}

	public Vector_3[] computeAllAttractiveForces() {

		int N = this.g.vertices.size();

		Vector_3[] attractiveForces = new Vector_3[N];
		for (int i = 0; i < N; i++) {
			attractiveForces[i] = this.computeAttractiveForce(this.g.vertices.get(i));
		}
		return attractiveForces;
	}

	public Vector_3 computePartialRepulsiveForce(Node u, Octree tree) {

		Vector_3 displacement = new Vector_3(0, 0, 0);
		Point_3 supernode = tree.baryCenter;
		Vector_3 delta = (Vector_3) supernode.minus(u.p);
		double distance = Math.sqrt((double) delta.squaredLength());

		if (tree.dimension / distance < 10) {
			displacement = delta.multiplyByScalar(tree.nodeList.size() * this.repulsiveForce(distance) / distance);
		}

		else if (tree.subCubes == null && !tree.nodeList.isEmpty()) {
			Node v = tree.nodeList.get(0);
			delta = new Vector_3(0, 0, 0);
			delta = (Vector_3) v.getPoint().minus(u.getPoint());
			distance = Math.sqrt((double) delta.squaredLength());
			delta = delta.divisionByScalar(distance);
			delta = delta.multiplyByScalar(this.repulsiveForce(distance));
			displacement = displacement.sum(delta);
		}

		else if (tree.subCubes != null) {
			for (int k = 0; k < 8; k++) {
				displacement = displacement.sum(this.computePartialRepulsiveForce(u, tree.subCubes.get(k)));
			}

		}

		return displacement.multiplyByScalar(this.C);

	}

	public Vector_3 computeTotalRepulsiveForce(Node u, Octree fulltree) {

		Vector_3 displacement = new Vector_3(0, 0, 0);
		Octree cell = fulltree.getSmallestContainer(u);
		Octree ancester = cell.parent;

		while (true) {

			for (Octree subcube : ancester.subCubes) {

				if (!subcube.containsNode(u)) {
					displacement = displacement.sum(this.computePartialRepulsiveForce(u, subcube));
				}

			}

			ancester = ancester.parent;
			if (ancester == null) {
				break;
			}

		}

		return displacement;
	}

	public Vector_3[] computeAllRepulsiveForces(Octree fulltree) {

		int N = this.g.vertices.size();
		Vector_3[] RepulsiveForces = new Vector_3[N];

		for (int i = 0; i < N; i++) {
			RepulsiveForces[i] = this.computeTotalRepulsiveForce(this.g.vertices.get(i), fulltree);
		}

		return RepulsiveForces;
	}

	public void computeLayout() {
		System.out.print("Performing iteration (fast FR91): " + this.iterationCount);
		long startTime = System.nanoTime(), endTime; // for evaluating time performances

		
		
			
		
		Octree fulltree = new Octree(this.g);

		Vector_3[] repulsiveForces = this.computeAllRepulsiveForces(fulltree);
		Vector_3[] attractiveForces = this.computeAllAttractiveForces();

		int N = this.g.sizeVertices();

		Vector_3 totalForce = new Vector_3(0,0,0);
		double norme=0;
		
		for (int k = 0; k < N; k++) {
			
			totalForce = repulsiveForces[k].sum(attractiveForces[k]);
			norme = Math.sqrt((double) totalForce.squaredLength());
			totalForce  = totalForce.divisionByScalar(norme);
			
			this.g.getNode(k).p = this.g.getNode(k).p.sum(totalForce.multiplyByScalar(this.temperature));

		}
		
		// evaluate time performances
		endTime = System.nanoTime();
		double duration = (double) (endTime - startTime) / 1000000000.;
		System.out.println("iteration " + this.iterationCount + " done (" + duration + " seconds)");

		this.cooling(); // update temperature

		this.iterationCount++; // increase counter (to count the number of performed iterations)
	}

	protected void cooling() {
		this.temperature = Math.max(this.temperature * coolingConstant, minTemperature);
		// this.temperature=Math.max(this.temperature-coolingConstant, minTemperature);
		// // variant
	}

	public String toString() {
		String result = "fast implementation of the force-directed algorihm: Fruchterman Reingold\n";
		result = result + "\t area= " + w + " x " + h + "\n";
		result = result + "\t k= " + this.k + "\n";
		result = result + "\t C= " + this.C + "\n";
		result = result + "\t initial temperature= " + this.temperature + "\n";
		result = result + "\t minimal temperature= " + this.minTemperature + "\n";
		result = result + "\t cooling constant= " + this.coolingConstant + "\n";

		return result;
	}

}
