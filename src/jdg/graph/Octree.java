// This Class is a simple implementation of the Octree data structure for Barnes and Hut version of the  FR-Algorithm.


package jdg.graph;

import java.util.ArrayList;

import Jcg.geometry.Point_3;
import jdg.graph.Node;

public class Octree implements Cloneable {

	public double dimension;
	public Point_3 origin;

	public Point_3 baryCenter;

	public ArrayList<Node> nodeList = new ArrayList<Node>();

	public Octree parent=null;

	public ArrayList<Octree> subCubes = null;


	public Octree(Point_3 orig, double dim) {
		
		this.origin = orig;
		this.dimension = dim;
		this.nodeList = new ArrayList<Node>();
		this.baryCenter = new Point_3();

	}
	
	
	public Octree(AdjacencyListGraph g) {
		
		double[] bounds = this.getBounds(g);
		this.origin = new Point_3(bounds[0],bounds[1],bounds[2]);;
		this.dimension = bounds[3];
		this.nodeList = new ArrayList<Node>();
		this.baryCenter = new Point_3();
		this.fillTree(g);

	
	}


	public void fillTree(AdjacencyListGraph g) {
		
		for(Node u: g.vertices) {
			this.insertVertex(u);
		}
		
	}
	
	public void instantiateSubCubes() {
		this.subCubes = new ArrayList<Octree>(8);
		
		
		Point_3 subOrigin = new Point_3(this.origin);
		Octree genericTree = new Octree(subOrigin, this.dimension/2);
		genericTree.parent = this;
		this.subCubes.add(genericTree);
		
		subOrigin = new Point_3(this.origin);
		subOrigin.setX(this.origin.x + this.dimension / 2);
		genericTree = new Octree(subOrigin, this.dimension/2);
		genericTree.parent = this;
		this.subCubes.add(genericTree);
		
		subOrigin = new Point_3(this.origin);
		subOrigin.setY(this.origin.y + this.dimension / 2);
		genericTree = new Octree(subOrigin, this.dimension/2);
		genericTree.parent = this;
		this.subCubes.add(genericTree);
		
		subOrigin = new Point_3(this.origin);
		subOrigin.setX(this.origin.x + this.dimension / 2);
		subOrigin.setY(this.origin.y + this.dimension / 2);
		genericTree = new Octree(subOrigin, this.dimension/2);
		genericTree.parent = this;
		this.subCubes.add(genericTree);
		
		subOrigin = new Point_3(this.origin);
		subOrigin.setZ(this.origin.z + this.dimension / 2);
		genericTree = new Octree(subOrigin, this.dimension/2);
		genericTree.parent = this;
		this.subCubes.add(genericTree);
		
		subOrigin = new Point_3(this.origin);
		subOrigin.setX(this.origin.x + this.dimension / 2);
		subOrigin.setZ(this.origin.z + this.dimension / 2);
		genericTree = new Octree(subOrigin, this.dimension/2);
		genericTree.parent = this;
		this.subCubes.add(genericTree);
		
		subOrigin = new Point_3(this.origin);
		subOrigin.setY(this.origin.y + this.dimension / 2);
		subOrigin.setZ(this.origin.z + this.dimension / 2);
		genericTree = new Octree(subOrigin, this.dimension/2);
		genericTree.parent = this;
		this.subCubes.add(genericTree);
		
		subOrigin = new Point_3(this.origin);
		subOrigin.setX(this.origin.x + this.dimension / 2);
		subOrigin.setY(this.origin.y + this.dimension / 2);
		subOrigin.setZ(this.origin.z + this.dimension / 2);
		genericTree = new Octree(subOrigin, this.dimension/2);
		genericTree.parent = this;
		this.subCubes.add(genericTree);
	}
	
	public void insertVertex(Node node) {

		if (!this.containsNode(node))
			return;

		int N = this.nodeList.size();
		this.nodeList.add(node);

		this.baryCenter.x *= N;
		this.baryCenter.x += node.p.x;
		this.baryCenter.x /= N + 1;

		this.baryCenter.y *= N;
		this.baryCenter.y += node.p.y;
		this.baryCenter.y /= N + 1;

		this.baryCenter.z *= N;
		this.baryCenter.z += node.p.z;
		this.baryCenter.z /= N + 1;
		
		N++;
		
		if (this.subCubes == null && N == 2) {
			this.instantiateSubCubes();

			for (Node n : this.nodeList) {
				this.subCubes.get(0).insertVertex(n);
				this.subCubes.get(1).insertVertex(n);
				this.subCubes.get(2).insertVertex(n);
				this.subCubes.get(3).insertVertex(n);
				this.subCubes.get(4).insertVertex(n);
				this.subCubes.get(5).insertVertex(n);
				this.subCubes.get(6).insertVertex(n);
				this.subCubes.get(7).insertVertex(n);
			}

		} else if (this.subCubes != null) {

			this.subCubes.get(0).insertVertex(node);
			this.subCubes.get(1).insertVertex(node);
			this.subCubes.get(2).insertVertex(node);
			this.subCubes.get(3).insertVertex(node);
			this.subCubes.get(4).insertVertex(node);
			this.subCubes.get(5).insertVertex(node);
			this.subCubes.get(6).insertVertex(node);
			this.subCubes.get(7).insertVertex(node);

		}

	}

	public boolean containsNode(Node node) {

		boolean boolx = this.origin.x <= node.p.x && node.p.x < (this.origin.x + this.dimension);
		boolean booly = this.origin.y <= node.p.y && node.p.y < (this.origin.y + this.dimension);
		boolean boolz = this.origin.z <= node.p.z && node.p.z < (this.origin.z + this.dimension);

		return (boolx && booly && boolz);
	}

	public Octree getSmallestContainer(Node node) {


		if(this.subCubes==null && this.containsNode(node)) {
			return this;
		}
		else if(this.containsNode(node)) {
			
			for(Octree subcube : this.subCubes) {
				
				if(subcube.containsNode(node)) return subcube.getSmallestContainer(node);
				
			}
		}
		
		return null;
		

	}

	
	public double[] getBounds(AdjacencyListGraph g) {
		
		double xmax=0;
		double ymax=0;
		double zmax=0;
		double xmin=0;
		double ymin=0;
		double zmin=0;
		
		for(Node u: g.vertices) {	
			if(u.p.x>xmax) {
				xmax=u.p.x+1;
			}
			if(u.p.y>ymax) {
				ymax=u.p.y+1;
			}
			if(u.p.z>zmax) {
				zmax=u.p.z+1;
			}
			
			
			
			if(u.p.x<xmin) {
				xmin=u.p.x-1;
			}
			if(u.p.y<ymin) {
				ymin=u.p.y-1;
			}
			if(u.p.z<zmin) {
				zmin=u.p.z-1;	
			}
		}
		
		double dimension = 2 * Math.max(Math.max(xmax - xmin, ymax - ymin),zmax - zmin);
		double[] res = {xmin,ymin,zmin,dimension};
		return res;
	}
	
	
	
	public String toString() {

		return (this.origin + " with dimension  " + this.dimension);
	}
}
