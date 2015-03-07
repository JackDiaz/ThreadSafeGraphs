/* Jack Diaz
 * 111499298
 */

package cmsc433.p1;

import java.util.ArrayList;
import java.util.List;

/**
 * An <code>PersistentGraph</code> extends the <code>Graph</code>
 * abstract class, adding methods to construct the graph. These
 * methods do not modify the existing graph, but instead return a new
 * graph that reflects the change. The original graph can be used as
 * before, unchanged, at the same time as old graph. The
 * implementation of the two graphs takes advantage of immutable data
 * structures whose contents can be shared safely between the old and
 * new graphs. All methods are atomic in the presence of access by
 * multiple threads.
 */
public class PersistentGraph extends Graph {
	final private ImmutableList<Node> nodes; // holds all the nodes
	final private ImmutableList<Edge> edges; // holds all the edges

	/* persistent graph constructor that is empty */
	private PersistentGraph(){
		nodes = null;
		edges = null;
	}

	/* persistent graph constructor that makes a graph that has
	 * all the nodes and edges from the previous graph and also
	 * the given node */ 
	private PersistentGraph(PersistentGraph g, Node n){
		if(g.getNodes() == null){
			nodes = new ImmutableList<Node>(n, null);
		}else if(g.getNodes().member(n)){
			nodes = g.getNodes();
		}else{
			nodes = g.getNodes().add(n);
		}
		edges = g.getEdges();
	}

	/* persistent graph constructor that makes a graph that has
	 * all the nodes and edges from the previous graph and also
	 * the given edge
	 * it also adds the nodes of the edge if they are not already present*/ 
	private PersistentGraph(PersistentGraph g, Edge e){
		ImmutableList<Node> gNodes = g.getNodes();
		Node src = e.getSource();
		Node dst = e.getDest();

		if(g.getEdges() == null){
			edges = new ImmutableList<Edge>(e, null);
		}else if(g.getEdges().member(e)){
			edges = g.getEdges();
		}else{
			edges = g.getEdges().add(e);
		}

		if(gNodes == null){
			gNodes = new ImmutableList<Node>(src, null);
			if(!src.equals(dst)){
				gNodes = gNodes.add(dst);
			}
			nodes = gNodes;
		}else{
			Boolean memSrc = gNodes.member(src);
			Boolean memDst = gNodes.member(dst);

			if(memSrc && memDst){
				nodes = gNodes;
			}else if(memSrc && !memDst){
				nodes = gNodes.add(dst);
			}else if(!memSrc && memDst){
				nodes = gNodes.add(src);
			}else{
				if(src.equals(dst)){
					nodes = gNodes.add(dst);
				}else{
					gNodes = gNodes.add(src);
					nodes = gNodes.add(dst);
				}

			}

		}
	}

	/* persistent graph constructor that makes a graph that has
	 * all the nodes and edges from the previous graph and does
	 * not have the given node 
	 * it also removes all edges going to and from that node*/ 
	private PersistentGraph(Node n, PersistentGraph g){
		if(g.getNodes() == null){
			nodes = null;
		}else{
			nodes = g.getNodes().remove(n);
		}
		ImmutableList<Edge> newEdges = g.getEdges();
		for(Edge e : g.getEdges()){
			if(e.getDest().equals(n) || e.getSource().equals(n)){
				newEdges = newEdges.remove(e);
			}
		}
		edges = newEdges;
	}

	/* persistent graph constructor that makes a graph that has
	 * all the nodes and edges from the previous graph and does
	 * not have the given edge */ 
	private PersistentGraph(Edge e, PersistentGraph g){
		nodes = g.getNodes();
		if(g.getEdges() == null){
			edges = null;
		}else{
			edges = g.getEdges().remove(e);
		}
	}

	private ImmutableList<Node> getNodes(){
		return nodes;
	}

	private ImmutableList<Edge> getEdges(){
		return edges;
	}


	/**
	 * @return an empty PersistentGraph
	 */
	public static PersistentGraph emptyGraph() {
		return new PersistentGraph();
	}

	/**
	 * Returns a new PersistentGraph that is the same as this graph
	 * but also contains Node n.
	 *
	 * @param n the Node to add
	 * @return a new graph containing n
	 */
	public PersistentGraph addNode(Node n) {
		return new PersistentGraph(this, n);
	}

	/**
	 * Returns a new PersistentGraph that is the same as this graph
	 * but also contains the given Edge (and its source and sink nodes
	 * if they are not already present).
	 *
	 * @param e the Edge to add
	 * @return a new graph containing e (and its constituent nodes)
	 */
	public PersistentGraph addEdge(Edge e) {
		return new PersistentGraph(this, e);
	}

	/**
	 * Returns a new PersistentGraph that is the same as this graph
	 * but has the given node (and edges to/from it) removed.
	 *
	 * @param n the node to remove
	 * @return a new graph without n (and any edges involving it)
	 */
	public PersistentGraph removeNode(Node n) {
		return new PersistentGraph(n, this);
	}

	/**
	 * Returns a new PersistentGraph that is the same as this graph
	 * but has the given edge removed
	 *
	 * @param e the edge to remove
	 * @return a new graph without e
	 */
	public PersistentGraph removeEdge(Edge e) {
		return new PersistentGraph(e, this);
	}

	/**
	 * Two PersistentGraphs are equal if they contain the same nodes and edges
	 *
	 * @param o The object to compare with
	 * @return true if both are PersistentGraphs and contain the same objects
	 */
	public boolean equals(Object other){
		if (other == null){
			return false;
		}

		if (other == this) {
			return true;
		}

		if (!(other instanceof PersistentGraph)) {
			return false;
		}

		if (this.getClass() != other.getClass()){
			return false;
		}

		PersistentGraph in = (PersistentGraph) other;

		/* if one has nodes == null and the other does not, return false
		 * or if they are both not null and they have different sizes return false
		 */
		if(in.getNodes() == null && nodes != null){
			return false;
		}else if(in.getNodes() != null && nodes == null){
			return false;
		}else if(in.getNodes() != null && nodes != null 
				&& in.getNodes().size() != nodes.size()){
			return false;
		}

		/* if one has edges == null and the other does not, return false
		 * or if they are both not null and they have different sizes return false
		 */
		if(in.getEdges() == null && edges != null){
			return false;
		}else if(in.getEdges() != null && edges == null){
			return false;
		}else if(in.getEdges() != null && edges != null 
				&& in.getEdges().size() != edges.size()){
			return false;
		}
		
		if(in.getNodes() != null && nodes != null){
			List<Node> otherNodes = in.iterNodes(nf(), new ArrayList<Node>());
			for(Node n : otherNodes){
				if(!nodes.member(n)){
					return false;
				}
			}
		}

		if(in.getEdges() != null && edges != null){
			List<Edge> otherEdges = in.iterEdges(ef(), new ArrayList<Edge>());
			for(Edge e : otherEdges){
				if(!edges.member(e)){
					return false;
				}
			}
		}

		return true;
	}

	// Methods to implement from the Graph abstract class

	public <A> A iterNodes(NodeFun<A> func, A accum) {
		A ret = accum;
		if(nodes != null){
			for(Node n : nodes){
				ret = func.run(n, ret);
			}
		}
		return ret;
	}

	public <A> A iterEdges(EdgeFun<A> func, A accum) {
		A ret = accum;
		if(edges != null){
			for(Edge e : edges){
				ret = func.run(e, ret);
			}
		}
		return ret;
	}

	public <A> A iterSuccessors(Node n, NodeFun<A> func, A accum) {
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

	public <A> A iterPredecessors(Node n, NodeFun<A> func, A accum) {
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
