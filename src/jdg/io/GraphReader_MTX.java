package jdg.io;

import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Paths;

import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;
import tc.TC;
import Jcg.geometry.Point_3;

/**
 * Provides methods for dealing with graphs stored in Matrix Market format.
 */		   
public class GraphReader_MTX extends GraphReader {

    /**
     * Read a graph stored in MTX format
     * 
     * Remark: nodes have indices between 1..n
     */		   
    public AdjacencyListGraph read(String filename) { 
    	System.out.print("Reading graph in MTX format ("+filename+")...");
    	TC.lectureDansFichier(filename);
    	String ligne=TC.lireLigne();
    	String[] tabFrom=TC.motsDeChaine(ligne);
    	
    	while(tabFrom[0].charAt(0)=='%') { // read file header (commented lines)
    		ligne=TC.lireLigne();
    		tabFrom=TC.motsDeChaine(ligne);
    	}
    	
		int n=Integer.parseInt(tabFrom[0]); // number of vertices
		
    	//System.out.println("m.vertices "+m.points.length);
    	//System.out.println("m.faces "+m.faces.length);
    	//System.out.println("m.halfedges "+m.sizeHalfedges);    	
    	
    	AdjacencyListGraph g=new AdjacencyListGraph();
    	int i=0;
    	//System.out.print("\tSetting vertices...");
    	while(i<n) { // read vertex indices
    		int index=i; // recall that vertices must have indices between 0 and n-1
    		Point_3 p=new Point_3(0., 0., 0.); // vertex coordinates are still not defined
    		Color color=null;
    		
    		g.addNode(new Node(index, p, color));
    		i++;
    	}
    	//System.out.println("done ("+g.sizeVertices()+")");
    	
    	//System.out.print("\tSetting edges...");
    	i=0;
    	while(TC.finEntree()==false) { // read edges
    		ligne=TC.lireLigne();
    		tabFrom=TC.motsDeChaine(ligne);
    		if(tabFrom!=null && tabFrom.length>0 && tabFrom[0].charAt(0)!='%') {
    			int index1=Integer.parseInt(tabFrom[0])-1; // recall that vertices have indices in 1..n in MTX format
    			int index2=Integer.parseInt(tabFrom[1])-1; // recall that vertices have indices in 1..n in MTX format
    			Node v1=g.getNode(index1);
    			Node v2=g.getNode(index2);
    			
    			if(v1==null || v2==null) {
    				throw new Error("Error: wrong vertex indices "+index1+" "+index2);
    			}
    			if(v1!=v2 && g.adjacent(v1, v2)==false && g.adjacent(v2, v1)==false) { // loops and multiple edges are not allowed
    				g.addEdge(v1, v2); // addEdge already adds the two edges (v1, v2) and (v2, v1)
    				i++;
    			}
    		}
    	}
    	//System.out.println("done ("+g.sizeEdges()+")");
    	
    	System.out.println("done ("+g.vertices.size()+" vertices, "+g.sizeEdges()+" edges)");
    	TC.lectureEntreeStandard();
    	return g;
    }
    
    /**
     * Read the geometric coordinates of nodes of a given graph g (storage format for coordinates is MTX)
     * 
     * @param g the input graph
     * @param inputData the file contained the vertex coordinates stored in MTX format
     */		   
    public void readGeometry(AdjacencyListGraph g, String inputData) {
    	if(g==null || inputData==null || Files.exists(Paths.get(inputData))==false) {
    		System.out.println("Warning: the file containing geometric data is not defined");
    		return;
    	}
    	System.out.print("Reading geometric coordinates stored in MTX format ("+inputData+")...");
    	TC.lectureDansFichier(inputData);
    	
    	String ligne=TC.lireLigne();
    	String[] tabFrom=TC.motsDeChaine(ligne);
    	while(tabFrom[0].charAt(0)=='%') { // read file header (commented lines)
    		ligne=TC.lireLigne();
    		tabFrom=TC.motsDeChaine(ligne);
    	}

		int n=Integer.parseInt(tabFrom[0]); // number of vertices
		int dim=Integer.parseInt(tabFrom[1]); // dimension of the space (2 or 3)
		
		if(g.sizeVertices()!=n)
			throw new Error("Error: the number of coordinates does not match the number of vertices");
		
		double[] x=new double[n];
		double[] y=new double[n];
		double[] z=new double[n];
		
		int i=0;
    	while(i<n) { // x coordinates
    		ligne=TC.lireLigne();
    		tabFrom=TC.motsDeChaine(ligne);
    		x[i]=Double.parseDouble(tabFrom[0]);
    		i++;
    	}
    	i=0;
    	while(i<n) { // y coordinates
    		ligne=TC.lireLigne();
    		tabFrom=TC.motsDeChaine(ligne);
    		y[i]=Double.parseDouble(tabFrom[0]);
    		i++;
    	}
    	if(dim==3){
        	i=0;
        	while(i<n) { // y coordinates
        		ligne=TC.lireLigne();
        		tabFrom=TC.motsDeChaine(ligne);
        		z[i]=Double.parseDouble(tabFrom[0]);
        		i++;
        	}   		
    	}

    	i=0;
    	for(Node v: g.vertices) { // set vertex coordinates in the graph
    		v.p.setX(x[i]);
    		v.p.setY(y[i]);
    		v.p.setZ(z[i]); // z-coordinate is 0 if dim=2
    		i++;
    	}
    	
    	System.out.println("done");
    	System.out.println("\tvertex coordinates loaded ("+g.sizeVertices()+")"+"[dimension "+dim+"]");
    	TC.lectureEntreeStandard();
    }

    /**
     * Read and set the colors of nodes of a given graph g (storage format for coordinates is MTX)
     * 
     * @param g the input graph
     * @param inputData the file contained the colors stored in MTX format
     */		   
    public static void readColors(AdjacencyListGraph g, String inputData) {
    	if(g==null || inputData==null || Files.exists(Paths.get(inputData))==false) {
    		System.out.println("Warning: the file containing geometric data is not defined");
    		return;
    	}
    	System.out.print("Reading geometric coordinates stored in MTX format ("+inputData+")...");
    	TC.lectureDansFichier(inputData);
    	
    	String ligne=TC.lireLigne();
    	String[] tabFrom=TC.motsDeChaine(ligne);
    	while(tabFrom[0].charAt(0)=='%') { // read file header (commented lines)
    		ligne=TC.lireLigne();
    		tabFrom=TC.motsDeChaine(ligne);
    	}

		int n=Integer.parseInt(tabFrom[0]); // number of vertices
		int dim=Integer.parseInt(tabFrom[1]); // dimension of the space (3)
		
		if(g.sizeVertices()!=n)
			throw new Error("Error: the number of colors does not match the number of vertices");
		
		double[] red=new double[n];
		double[] green=new double[n];
		double[] blue=new double[n];
		
		int i=0;
    	while(i<n) { // x coordinates
    		ligne=TC.lireLigne();
    		tabFrom=TC.motsDeChaine(ligne);
    		red[i]=Double.parseDouble(tabFrom[0]);
    		i++;
    	}
    	i=0;
    	while(i<n) { // y coordinates
    		ligne=TC.lireLigne();
    		tabFrom=TC.motsDeChaine(ligne);
    		green[i]=Double.parseDouble(tabFrom[0]);
    		i++;
    	}
    	if(dim==3){
        	i=0;
        	while(i<n) { // blue component
        		ligne=TC.lireLigne();
        		tabFrom=TC.motsDeChaine(ligne);
        		blue[i]=Double.parseDouble(tabFrom[0]);
        		i++;
        	}   		
    	}

    	i=0;
    	for(Node v: g.vertices) { // set vertex coordinates in the graph
    		v.color=new Color((float)red[i], (float)green[i], (float)blue[i]);
    		i++;
    	}
    	
    	System.out.println("done");
    	System.out.println("\tvertex colors loaded ("+g.sizeVertices()+")"+"[dimension "+dim+"]");
    	TC.lectureEntreeStandard();
    }

}
