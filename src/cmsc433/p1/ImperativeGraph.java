/* Jack Diaz
 * 111499298
 */

package cmsc433.p1;

import java.util.ArrayList;
import java.util.List;

/**
 * An <code>ImperativeGraph</code> extends the <code>Graph</code>
 * abstract class, adding methods to construct the graph. These
 * methods modify the graph in place, hence the class's name. The
 * class should be thread-safe, so that execution of all methods are
 * atomic in the presence of access by multiple threads.
 */
public class ImperativeGraph extends Graph {
	private ImmutableList<Node> nodes; // Guarded by nodeLock1
	private ImmutableList<Edge> edges; // Guarded by edgeLock2

	private final Object nodeLock1; // Guards nodes, comes before edgeLock2
	private final Object edgeLock2; // Guards edges, comes after nodeLock1

	/**
	 * Creates a new imperative graph
	 */
	public ImperativeGraph() {
		nodes = null;
		edges = null;
		nodeLock1 = new Object();
		edgeLock2 = new Object();
	}

	private ImperativeGraph(ImmutableList<Node> nodesIn, ImmutableList<Edge> edgesIn){
		nodes = nodesIn;
		edges = edgesIn;
		nodeLock1 = new Object();
		edgeLock2 = new Object();
	}

	/**
	 * Adds the given node to the graph, if it is not already there.
	 *
	 * @param n The node to add to the graph
	 */
	public void addNode(Node n) {
		synchronized(nodeLock1){
			if(nodes == null){
				nodes = new ImmutableList<Node>(n, null);
			}else if(!nodes.member(n)){
				nodes = nodes.add(n);
			}
			return;
		}
	}

	/**
	 * Adds the given edge (u,v) to the graph, if it is not already
	 * there, and also adds nodes u and v to the graph if they are not
	 * there.
	 *
	 * @param e The edge to add to the graph
	 */    
	public void addEdge(Edge e) {
		synchronized(nodeLock1){
			synchronized(edgeLock2){
				if(edges == null){
					edges = new ImmutableList<Edge>(e, null);
				}else if(!edges.member(e)){
					edges = edges.add(e);
				}

				Node src = e.getSource();
				Node dst = e.getDest();
				this.addNode(src);
				this.addNode(dst);
			/* Why did I ever do it this way? What was I thinking?
			 * I hope I didn't have a good reason for doing this.
			 * if(nodes == null){
					if(src.equals(dst)){
						nodes = new ImmutableList<Node>(src, null);
					}else{
						nodes = new ImmutableList<Node>(src, null);
						nodes = nodes.add(dst);
					}
				}else{
					Boolean memSrc = nodes.member(src);
					Boolean memDst = nodes.member(dst);

					if(memSrc && !memDst){
						nodes = nodes.add(dst);
					}else if(!memSrc && memDst){
						nodes = nodes.add(src);
					}else if(!memSrc && !memDst){
						if(src.equals(dst)){
							nodes = nodes.add(dst);
						}else{
							nodes = nodes.add(src);
							nodes = nodes.add(dst);
						}
					}
				}*/
				return;
			}
		}
	}

	/**
	 * Removes the given node to the graph, if it is there, and also
	 * removes all edges that reference that node, if any.
	 *
	 * @param n The node to remove from the graph
	 * @return true if the node was in the graph, and was removed; false otherwise
	 */
	public boolean removeNode(Node n) {
		synchronized(nodeLock1){
			synchronized(edgeLock2){
				if(edges != null){
					ImmutableList<Edge> newEdges = edges;
					for(Edge e : edges){
						if(e.getDest().equals(n) || e.getSource().equals(n)){
							newEdges = newEdges.remove(e);
						}
					}
					edges = newEdges;
				}

				if(nodes == null){
					return false;
				}else if(nodes.member(n)){
					nodes = nodes.remove(n);
					return true;
				}else{
					return false;
				}
			}
		}
	}

	/**
	 * Removes the given edge from the graph, if it is there.
	 *
	 * @param e The node to remove from the graph
	 * @return true if the edge was in the graph, and was removed; false otherwise
	 */
	public boolean removeEdge(Edge e) {
		synchronized(edgeLock2){
			if(edges == null){
				return false;
			}else if(edges.member(e)){
				edges = edges.remove(e);
				return true;
			}else{
				return false;
			}
		}
	}

	/**
	 * Removes all nodes and edges from the graph.
	 */
	public void clear() {
		synchronized(nodeLock1){
			synchronized(edgeLock2){
				edges = null;
				nodes = null;
				return;
			}
		}
	}

	/**
	 * Makes a copy of the graph, so that additions or removals to the
	 * original are not seen in the copy, and vice versa.
	 */
	public ImperativeGraph copy() {
		synchronized(nodeLock1){
			synchronized(edgeLock2){
			/* I was worried about making a shallow copy 
			 * but then I realized my state objects are immutable 
			 * so I went back to how I initially had it
			 * 
			 * ImperativeGraph ret = new ImperativeGraph();
				for(Edge e : edges){
					ret.addEdge(e);
				}
				for(Node n : nodes){
					ret.addNode(n);
				}
				return ret;*/
				return new ImperativeGraph(nodes, edges);
			}
		}
	}

	// Methods inherited from Graph abstract class

	public <A> A iterNodes(NodeFun<A> func, A accum) {
		synchronized(nodeLock1){
			A ret = accum;
			if(nodes != null){
				for(Node n : nodes){
					ret = func.run(n, ret);
				}
			}
			return ret;
		}
	}
	public <A> A iterEdges(EdgeFun<A> func, A accum) {
		synchronized(edgeLock2){
			A ret = accum;
			if(edges != null){
				for(Edge e : edges){
					ret = func.run(e, ret);
				}
			}
			return ret;
		}
	}
	public synchronized <A> A iterSuccessors(Node n, NodeFun<A> func, A accum) {
		synchronized(edgeLock2){
			A ret = accum;
			if(edges != null){
				for(Edge e : edges){
					if(e.getSource().equals(n)){
						ret = func.run(e.getDest(), ret);
					}
				}
			}
			return ret;
		}
	}
	public synchronized <A> A iterPredecessors(Node n, NodeFun<A> func, A accum) {
		synchronized(edgeLock2){
			A ret = accum;
			if(edges != null){
				for(Edge e : edges){
					if(e.getDest().equals(n)){
						ret = func.run(e.getSource(), ret);
					}
				}
			}
			return ret;
		}
	}

	/**
	 * Converts an ImperativeGraph to a PersistentGraph
	 */
	public synchronized PersistentGraph toPersistentGraph() {
		PersistentGraph ret = PersistentGraph.emptyGraph();
		synchronized(nodeLock1){
			synchronized(edgeLock2){
				for(Edge e : edges){
					ret = ret.addEdge(e);
				}

				for(Node n : nodes){
					ret = ret.addNode(n);
				}
				return ret;
			}
		}
	}

	/**
	 * Two ImperativeGraphs are equal if they contain the same nodes and edges
	 *
	 * @param o The object to compare with
	 * @return true if both are ImperativeGraphs and contain the same objects
	 */
	public synchronized boolean equals(Object o) {
		if (o == null){
			return false;
		}

		if (o == this) {
			return true;
		}

		if (!(o instanceof ImperativeGraph)) {
			return false;
		}

		if (this.getClass() != o.getClass()){
			return false;
		}

		ImperativeGraph in = ((ImperativeGraph) o).copy();

		/* if one has nodes == null and the other does not, return false
		 * or if they are both not null and they have different sizes return false
		 */
		synchronized(nodeLock1){
			synchronized(edgeLock2){
				if(in.nodes == null && nodes != null){
					return false;
				}else if(in.nodes != null && nodes == null){
					return false;
				}else if(in.nodes != null && nodes != null 
						&& in.nodes.size() != nodes.size()){
					return false;
				}


				/* if one has edges == null and the other does not, return false
				 * or if they are both not null and they have different sizes return false
				 */

				if(in.edges == null && edges != null){
					return false;
				}else if(in.edges != null && edges == null){
					return false;
				}else if(in.edges != null && edges != null 
						&& in.edges.size() != edges.size()){
					return false;
				}

				if(in.nodes != null && nodes != null){
					List<Node> otherNodes = in.iterNodes(nf(), new ArrayList<Node>());
					for(Node n : otherNodes){
						if(!nodes.member(n)){
							return false;
						}
					}
				}

				if(in.edges != null && edges != null){
					List<Edge> otherEdges = in.iterEdges(ef(), new ArrayList<Edge>());
					for(Edge e : otherEdges){
						if(!edges.member(e)){
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private static NodeFun<List<Node>> nf(){
		//function to return all the nodes in a list
		NodeFun<List<Node>> nf = new NodeFun<List<Node>>(){
			public List<Node> run(Node n, List<Node> l){
				l.add(n);
				return l;
			}};
			return nf;
	}

	private static EdgeFun<List<Edge>> ef(){
		//function to return all the edges in a list
		EdgeFun<List<Edge>> ef = new EdgeFun<List<Edge>>(){
			public List<Edge> run(Edge e, List<Edge> l){
				l.add(e);
				return l;
			}};
			return ef;
	}
}