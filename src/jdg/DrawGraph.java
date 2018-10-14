package jdg;

import processing.core.*;
import jdg.clustering.CommunityDetection;
import jdg.clustering.GreedyAlgorithm;
import jdg.clustering.LouvainAlgorithm;
import jdg.clustering.RandomCommunities;
import jdg.graph.AdjacencyListGraph;
import jdg.graph.Node;
import jdg.layout.*;

import java.awt.Color;

import Jcg.geometry.Point_2;
import Jcg.geometry.Vector_2;

/**
 * A class for drawing (dynamic) graphs (using Processing 1.5.1)
 *
 * @author Luca Castelli Aleardi (Ecole Polytechnique, INF421, october 2017)
 */
public class DrawGraph extends PApplet {
    // coordinates of the bounding box
    protected double xmin=Double.MAX_VALUE, xmax=Double.MIN_VALUE, ymin=Double.MAX_VALUE, ymax=Double.MIN_VALUE;

    // parameters for edge rendering
    double boundaryThickness=0.5;
    private int backgroundColor=255;
    private int edgeColor=50;
    private int edgeOpacity=200;
        
    /** node selected with mouse click (to show)  */
    public Node selectedNode=null; 
	public Point_2 current; // coordinates of the selected point
    
    /** Layout algorithm  */
    public Layout layoutFR91, layoutFastFR91;
    
    /** Community detection methods */
    public CommunityDetection louvain, greedy, random;
    public Color[] randomColors=null;

    /** input graph to draw */
    static public AdjacencyListGraph inputGraph;
        	
   	// parameters of the 2d frame/canvas
    public static int sizeX=800; // horizontal size of the canvas (pizels)
    public static int sizeY=800; // vertical size of the canvas (pixels)
    Point_2 a,b; // range of the window (left bottom and right top corners)
	  
	  /**
	   * Initialize the frame
	   */
	  public void setup() {
		  if(this.inputGraph==null) {
			System.err.println("Warning: the input network is not defined");
			System.out.println("Please be sure to run the program in the right way, as in the examples below:");
			System.out.println("\t example 1: java -jar NetworkVisualization data/facebook.mtx");
			System.out.println("\t example 2: java -jar NetworkVisualization data/facebook.mtx data/facebook_coord.mtx");
			System.exit(0);
		  }

		  System.out.println("Setting Canvas size: "+sizeX+" x "+sizeY);
		  this.size(sizeX,sizeY); // set the size of the Java Processing frame
		  
		  // set drawing parameters (size and range of the drawing layout)
		  double w2=sizeX/2.0;
		  double h2=sizeY/2.0;
		  this.a=new Point_2(-w2, -h2); // left bottom corner (the drawing region is centered at the origin)
		  this.b=new Point_2(w2, h2); // top right corner of the drawing region
		  
		  int n=this.inputGraph.sizeVertices();
	      this.randomColors=new Color[n];
	      for(int j=0;j<n;j++) {
	      	int r=(int)(Math.random()*255);
	       	int g=(int)(Math.random()*255);
	       	int b=(int)(Math.random()*255);
	       	this.randomColors[j]=new Color(r, g, b);
	       }
		    	      
	      // set the graph layout method
	      this.layoutFR91=new FR91Layout(inputGraph, sizeX, sizeY); // force-directed method (Fruchterman Reingold)
	      this.layoutFastFR91=new FastFR91Layout(inputGraph, sizeX, sizeY); // force-directed method (Fruchterman Reingold)
	      
	      // set the clustering methods
	      this.louvain=new LouvainAlgorithm(); // Louvain algorithm
	      this.greedy=new GreedyAlgorithm(); // Greedy algorithm
	      this.random=new RandomCommunities(5); // random partition
	  }

	  /**
	   * Run the Louvain algorithm for community detection
	   */
	  public void runLouvainAlgorithm() {
		  int[] communities=this.louvain.computeClusters(this.inputGraph); // partition into communities

		  // assign colors to vertices (according to the partition into communities)
		  for(Node u: this.inputGraph.vertices) {
			  int cluster=communities[u.index];
			  u.color=this.randomColors[cluster];
		  }
		  
		  System.out.println("Modularity="+this.louvain.computeModularity(this.inputGraph, communities));
	  }
	  
	  /**
	   * Run the Greedy algorithm for community detection
	   */
	  public void runGreedyAlgorithm() {
		  int[] communities=this.greedy.computeClusters(this.inputGraph); // partition into communities

		  // assign colors to vertices (according to the partition into communities)
		  for(Node u: this.inputGraph.vertices) {
			  int cluster=communities[u.index];
			  u.color=this.randomColors[cluster];
		  }
		  
		  System.out.println("Modularity="+this.greedy.computeModularity(this.inputGraph, communities));
	  }

	  /**
	   * Compute a random partition
	   */
	  public void computeRandomCommunities() {
		  int[] communities=this.random.computeClusters(this.inputGraph); // random partition into communities

		  // assign colors to vertices (according to the partition into communities)
		  for(Node u: this.inputGraph.vertices) {
			  int cluster=communities[u.index];
			  u.color=this.randomColors[cluster];
		  }
		  
		  System.out.println("Modularity="+this.random.computeModularity(this.inputGraph, communities));
	  }

	  /**
	   * Main method for drawing the applet
	   */
	  public void draw() {
	    this.background(this.backgroundColor); // set the color of background
	    
	    this.display2D(); // draw all edges in gray

	    if(this.selectedNode!=null) {
	    	this.drawVertexLabel(this.selectedNode);
	    }
	    
	    this.drawOptions();
	  }
	  
	  /**
	   * Deal with keyboard events
	   */
	  public void keyPressed(){
		  switch(key) {
		  	case('z'):this.updateBoundingBox(); break;
		  	case('c'):this.layoutFR91.computeLayout(); this.inputGraph = this.layoutFR91.g; break;
		  	case('f'):this.layoutFastFR91.computeLayout(); this.inputGraph = this.layoutFastFR91.g; break;
		  	case('o'):this.zoom(1.2); break;
		  	case('i'):this.zoom(0.8); break;
		  	case('l'):this.runLouvainAlgorithm(); break;
		  	case('g'):this.runGreedyAlgorithm(); break;
		  	case('r'):this.computeRandomCommunities(); break;
		  	default: System.out.println("Warning: this option is not supported");
		  }
	  }
	  
	  public void zoom(double factor) {
		  Point_2 barycenter=Point_2.midPoint(a, b);
		  Vector_2 vA=(Vector_2)barycenter.minus(a);
		  Vector_2 vB=(Vector_2)barycenter.minus(b);
		  vA=vA.multiplyByScalar(factor);
		  vB=vB.multiplyByScalar(factor);
		  a=barycenter.sum(vA);
		  b=barycenter.sum(vB);
	  }
	  	  
	  public void mouseClicked() {
		  if(mouseButton==LEFT) { // select a vertex (given its 2D position)
			  this.selectedNode=this.selectNode(mouseX, mouseY);
			  if(selectedNode!=null)
				  System.out.println("vertex "+selectedNode.index);
		  }
	  }

	  public void mousePressed() {
		  this.current=new Point_2(mouseX, mouseY);
	  }
	  
	  public void mouseReleased() {
	  }
	  
	  public void mouseDragged() {
		  if(mouseButton==RIGHT) { // translate the window
			  double norm=Math.sqrt(this.a.squareDistance(this.b).doubleValue());
			  double scaleFactor=norm/(this.sizeX);
			  
			  double deltaX=(mouseX-current.getX().doubleValue())*(scaleFactor);
			  double deltaY=(current.getY().doubleValue()-mouseY)*(scaleFactor);
		  
			  this.a.translateOf(new Vector_2(-deltaX, -deltaY)); // update the left bottom and right top vertices
			  this.b.translateOf(new Vector_2(-deltaX, -deltaY));
		  
			  this.current=new Point_2(mouseX, mouseY);
		  }
	  }
		
	    /**
	     * Update of the bounding box
	     */    
	    protected void update(double x, double y) {
	    	if (x<xmin)
	    		xmin = x-boundaryThickness;
	    	if (x>xmax)
	    		xmax = x+boundaryThickness;
	    	if (y<ymin)
	    		ymin = y-boundaryThickness;
	    	if (y>ymax)
	    		ymax = y+boundaryThickness;
	    }

	    /**
	     * Update the range of the drawing region (defined by corners points 'a' and 'b')
	     */    
	    public void updateBoundingBox() {
	    	/*
	    	for(Node u: inputGraph.vertices) {
	    		Point_3 p=u.getPoint();
	    		update(p.getX().doubleValue(), p.getY().doubleValue());
	    	}
	    	a=new Point_2(xmin, ymin);
	    	b=new Point_2(xmax, ymax);
	    	*/
	    }
	    
	    /**
	     * Return the current coordinates of the bounding box
	     */    
	    public double[] boundingBox() {
	    	return new double[] {xmin, xmax, ymin, ymax};
	    }

		/**
		 * Return the integer coordinates of a pixel corresponding to a given point
		 * 
		 * Warning: we must take care of the following parameters:
		 * -) the size of the canvas
		 * -) the size of bottom and left panels
		 * -) the negative direction of y-coordinates (in java drawing)
		 */
		public int[] getPoint(Point_2 v) {
			double x=v.getX().doubleValue(); // coordinates of point v
			double y=v.getY().doubleValue();
			double xRange=b.getX().doubleValue()-a.getX().doubleValue(); // width and height of the drawing area
			double yRange=b.getY().doubleValue()-a.getY().doubleValue();
			int i= (int) (this.sizeX*( (x-a.getX().doubleValue()) / xRange )); // scale with respect to the canvas dimension
			int j= (int) (this.sizeY*( (y-a.getY().doubleValue()) / yRange ));
			//i=i+this.horizontalShift;
			j=this.sizeY-j; // y = H - py;
			
			int[] res=new int[]{i, j};
			return res;
		}
		
		  /**
		   * Draw a gray edge (u, v)
		   */
		  public void drawSegment(Point_2 u, Point_2 v) {		  
			int[] min=getPoint(u);
			int[] max=getPoint(v);
		    
			this.stroke(edgeColor, edgeOpacity);
		    this.line(	(float)min[0], (float)min[1], 
		    			(float)max[0], (float)max[1]);
		  }

		  /**
		   * Draw a colored edge (u, v)
		   */
		  public void drawColoredSegment(Point_2 u, Point_2 v, int r, int g, int b) {		  
			int[] min=getPoint(u);
			int[] max=getPoint(v);
		    
			this.stroke(r, g, b, edgeOpacity);
		    this.line(	(float)min[0], (float)min[1], 
		    			(float)max[0], (float)max[1]);
		  }

		  /**
		   * Draw a vertex u on the canvas
		   */
		  public void drawVertex(Node u, double distortion, double maxDistortion) {
			  double ux=u.getPoint().getX().doubleValue();
			  double uy=u.getPoint().getY().doubleValue();
			  double maxValue;
			  
			int[] min=getPoint(new Point_2(ux, uy)); // pixel coordinates of the point in the frame
		    
			//System.out.println("v"+u.index+" dist: "+distortion+" max: "+maxDistortion);
			
			this.stroke(50, 255); // border color
			if(u.color==null)
				this.fill(50, 50, 50, 255); // node color
			else
				this.fill(u.color.getRed(), u.color.getGreen(), u.color.getBlue());
			
			int vertexSize=8; // basic vertex size
			//double growingFactor=1.+(distortion*10.);
			//vertexSize=(int)(3+vertexSize*growingFactor);
			this.ellipse((float)min[0], (float)min[1], vertexSize, vertexSize);
		  }

		  /**
		   * Draw a vertex label on the canvas (close to the node location)
		   */
		  public void drawVertexLabel(Node u) {
			  double ux=u.getPoint().getX().doubleValue();
			  double uy=u.getPoint().getY().doubleValue();			  
			  
			int[] min=getPoint(new Point_2(ux, uy)); // pixel coordinates of the point in the frame
		    			
			String label=this.getVertexLabel(u); // retrieve the vertex label to show
			
			//this.stroke(edgeColor, edgeOpacity);
			this.fill(200);
			this.rect((float)min[0], (float)min[1], 40, 30); // fill a gray rectangle
			this.fill(0);
			this.text(label, (float)min[0]+5, (float)min[1]+14); // draw the vertex label
		  }

		  /**
		   * Show options on the screen
		   */
		  public void drawOptions() {
			String label="press 'c' for performing one iteration of FR91\n";
			label=label+"press 'f' for one iteration of Fast FR91 (with octrees)\n";
			//label=label+"press 'e' (or 'd') to enable (or to disable) cooling process\n";
			label=label+"press 'l' to run the Louvain algorithm \n";
			label=label+"press 'g' to run the Greedy algorithm \n";
			label=label+"press 'r' to compute a random partition \n";

			String label2="press 'i' or 'o' for zoom\n"; // text to show
			label2=label2+"use 'left mouse click' to show vertex index\n";
			label2=label2+"press the 'right button' of the mouse to drag the layout";

			int posX=2;
			int posY=2;
			int textHeight=70;
			
			//this.stroke(edgeColor, edgeOpacity);
			this.fill(240);
			this.rect((float)posX, (float)posY, 380, textHeight); // fill a gray rectangle
			this.rect((float)390, (float)posY, 380, textHeight); // fill a gray rectangle
			this.fill(0);
			this.text(label, (float)posX+2, (float)posY+10); // draw the text
			this.text(label2, (float)posX+400, (float)posY+10); // draw the text
		  }

		  /**
		   * Select the vertex whose 2d projection is the closest to pixel (i, j)
		   */
		  public Node selectNode(int i, int j) {			  
			  Node result=null;
			  
			  double minDist=40.;
			  for(Node u: inputGraph.vertices) { // iterate over the vertices of g
				  Point_2 p=new Point_2(u.getPoint().getX(), u.getPoint().getY());
				  int[] q=this.getPoint(p);
				  
				  double dist=Math.sqrt((q[0]-i)*(q[0]-i)+(q[1]-j)*(q[1]-j));
				  if(dist<minDist) {
					  minDist=dist;
					  result=u;
				  }
			  }
			  
			  this.selectedNode=result;
			  return result;
		  }
		  
		  /**
		   * Draw the skeleton of a graph in 2D using a Processing frame
		   */
		  public void display2D() {
			  if(this.inputGraph==null)
				  return;
			  AdjacencyListGraph graph=this.inputGraph; // current graph to draw
			  if(graph==null) // if the graph is not defined exit
				  return;
			  
			  this.fill(255,100);
				for(Node u: graph.vertices) { // draw the edges of g
					Point_2 p=new Point_2(u.getPoint().getX(), u.getPoint().getY());
					for(Node v: u.neighbors) {
						if(v!=null && v.index>u.index) { // draw only directed edges (u, v) such that u<v
							Point_2 q=new Point_2(v.getPoint().getX(), v.getPoint().getY());
								this.drawSegment(p, q); // draw a gray edge
						}
					}
				}
				
				for(Node u: graph.vertices) { // finally draw the vertices of g
					this.drawVertex(u, 0, 1.); // color map is not computed
				}

		  }
		  
		  /**
		   * Compute the label of a vertex, from its index, spectral distortion and vertex age
		   */
		  public String getVertexLabel(Node u) {
			  String label="v"+u.index;
			  return label;
		  }
		  
			/**
			 * Return an "approximation" (as String) of a given real number (with a given numeric precision)
			 */
			private static String approxNumber(double a, int precision) {
				String format="%."+precision+"f";
				String s=String.format(format,a);
				return s;
			}
			
			public static void main(String[] args) {
				System.err.println("Warning: this class is not runnable");
				System.exit(0);
			}

}
