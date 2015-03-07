/* Jack Diaz
 * 111499298
 */

package cmsc433.p1;

import java.util.HashMap;

/**
 * An <code>Node</code> is an object that represents a vertex in a
 * graph (and is used by classes that extend the <code>Graph</code>
 * abstract class).
 */
public class Edge {
	private final Node from, to;
	private String str = null;
	private static HashMap<Edge, Edge> cache;
	
	private Edge(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Factory method that returns an Edge object
	 * for the two nodes.
	 *
	 * @param from - the source node
	 * @param to - the destination node
	 * @returns an Edge object bewteen the two nodes
	 */
	public static synchronized Edge create(Node from, Node to) {
		if(cache == null){
			cache = new HashMap<Edge, Edge>();
		}
		Edge wrkEdge = new Edge(from,to);
		if(cache.containsKey(wrkEdge)){
			return cache.get(wrkEdge);
		}else{
			cache.put(wrkEdge, wrkEdge);
			return wrkEdge;
		}
	}

	/**
	 * Returns the source node of the edge
	 *
	 * @returns source node of this Edge object 
	 */    
	public Node getSource() { return from; }

	/**
	 * Returns the destination node of the edge
	 *
	 * @returns destination node of this Edge object 
	 */    
	public Node getDest() { return to; }

	public boolean equals(Object v) {
		if (v instanceof Edge) {
			Edge e = (Edge) v;
			return this.from.equals(e.from) && this.to.equals(e.to);
		}
		else {
			return false;
		}
	}
	
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	public String toString() {
		if (str == null)
			str = (from.toString() + " -> " + to.toString());
		return str;
	}
}
