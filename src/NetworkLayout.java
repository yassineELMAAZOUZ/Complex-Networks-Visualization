import processing.core.PApplet;
import jdg.DrawGraph;
import jdg.graph.AdjacencyListGraph;
import jdg.io.GraphReader;
import jdg.io.GraphReader_MTX;
import jdg.layout.Layout;

/**
 * A program for computing network layouts with the "spring embedder" paradigm
 * 
 * This program requires one parameter: the input network, stored in Matrix Market format (.mtx)
 *
 * @author Luca Castelli Aleardi (Ecole Polytechnique, INF421, 2017)
 */

public class NetworkLayout extends DrawGraph {
	
	/**
	 * For running the PApplet as Java application
	 */
	public static void main(String args[]) {
		System.out.println("PI Network visualization (INF421, 2017)");
		if(args.length==0 || args.length>2) {
			System.out.println("Error: wrong arguments, one (or two) parameter(s) required");
			System.out.println("Usage example 1:  java -jar NetworkLayout data/network.mtx");
			System.out.println("Usage example 2:  java -jar NetworkLayout data/network.mtx data/network_coord.mtx");

			System.exit(0);
		}
		if(args[0].endsWith(".mtx")==false) {
			System.out.println("Error: wrong input format (MTX format supported)");
			System.exit(0);
		}
		if(args.length==2 && args[1].endsWith(".mtx")==false) {
			System.out.println("Error: wrong input format (MTX format supported)");
			System.exit(0);			
		}
		
		String filename=args[0];
		GraphReader reader=new GraphReader_MTX(); // open networks stores in Matrix Market format (.mtx)
		AdjacencyListGraph g=reader.read(filename); // read input network from file
		if(args.length==2) {
			String inputCoordinates=args[1];
			reader.readGeometry(g, inputCoordinates);
		}
		else {
			
			Layout.setRandomPoints(g, 400, 400);
		}
		
		DrawGraph.inputGraph=g; // set the input network

		PApplet.main(new String[] { "jdg.DrawGraph" }); // start the Processing viewer
	}

}
