/* Jack Diaz
 * 111499298
 */

package cmsc433.p1;

import java.io.Writer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>Graph</code> abstract class is used to represent a directed
 * Graph.  Each graph consists of a collection of Node objects and
 * directed edges between them.  An edge from <code>u</code> to
 * <code>v</code> is denoted as the pair <code>(u,v)</code> below.  For
 * all nodes <code>u,v</code> in the graph, there can be at most one
 * edge from node <code>u</code> to <code>v</code> (including from a
 * node to itself).
 * <p>

 * As edges are directed, each node in the graph may have zero or
 * more <i>predecessors</i> and zero or more <i>successors</i>.  Given
 * a node <code>v</code> in some graph <code>g</code>, the
 * predecessors of <code>v</code> are nodes <code>u1,...,un</code>,
 * not including <code>v</code>, where the edges <code>(u1,v),
 * ...,(un,v)</code> are in the graph <code>g</code>.  Similarly, the
 * successors of <code>v</code> are nodes <code>w1,...,wn</code>,
 * not include <code>v</code>, where the edges <code>(v,w1),
 * ...,(v,wn)</code> are in the graph <code>g</code>.
 */
public abstract class Graph {

	/**
	 * This function executes the given function on every node in the
	 * graph, accumulating and returning a result. The order that
	 * nodes are traversed is undefined.
	 *
	 * @param func This function takes a node and the current
	 * accumulator, and returns the new accumulator
	 * @param accum The initial value of the accumulator
	 */
	abstract public <A> A iterNodes(NodeFun<A> func, A accum);

	/**
	 * This function executes the given function on every edge in the
	 * graph, accumulating and returning a result. The order that
	 * edges are traversed is undefined.
	 *
	 * @param func This function takes an edge and the current
	 * accumulator, and returns the new accumulator
	 * @param accum The initial value of the accumulator
	 */
	abstract public <A> A iterEdges(EdgeFun<A> func, A accum);

	/**
	 * This function executes the given function on every node that is
	 * a successor to the given node in the graph, accumulating and
	 * returning a result. The order that nodes are traversed is
	 * undefined.
	 *
	 * @param n The node whose successors will be traversed
	 * @param func This function takes a node and the current
	 * accumulator, and returns the new accumulator
	 * @param accum The initial value of the accumulator
	 */
	abstract public <A> A iterSuccessors(Node n, NodeFun<A> func, A accum);

	/**
	 * This function executes the given function on every node that is
	 * a predecessor to the given node in the graph, accumulating and
	 * returning a result. The order that nodes are traversed is
	 * undefined.
	 *
	 * @param n The node whose predecessors will be traversed
	 * @param func This function takes a node and the current
	 * accumulator, and returns the new accumulator
	 * @param accum The initial value of the accumulator
	 */
	abstract public <A> A iterPredecessors(Node n, NodeFun<A> func, A accum);

	/**
	 * Determines whether the graph is empty (i.e., has no nodes and no edges)
	 *
	 * @return true if the graph has no nodes (and therefore, no edges either)
	 */
	public boolean isEmpty() {

		if(iterNodes(nf(), new ArrayList<Node>()).size() == 0){
			return true;
		}
		return false;

	}

	/**
	 * Returns the number of successors to the given node
	 *
	 * @param n The node to consider
	 * @return the number of successors of the given node
	 */
	public int outDegree(Node n) {
		return iterSuccessors(n, nf(), new ArrayList<Node>()).size();
	}

	/**
	 * Returns the number of predecessors to the given node
	 *
	 * @param n The node to consider
	 * @return the number of predecessors of the given node
	 */
	public int inDegree(Node n) {
		return iterPredecessors(n, nf(), new ArrayList<Node>()).size();
	}

	/**
	 * Ouputs the graph in DOT format, which using the DOT tools
	 * can be turned into a visualization of the graph.
	 *
	 * @param w The Writer to serialize the graph to
	 */
	public void outputDot(Writer w) throws IOException {
		final Writer w0 = w;
		w0.write("digraph G {\n");
		NodeFun<IOException> printNode = new NodeFun<IOException> () {
			public IOException run(Node n, IOException exn) {
				if (exn == null) {
					try {
						w0.write(n.toString());
						w0.write(";\n");
					} catch (IOException exn0) {
						exn = exn0;
					}
				}
				return exn;
			}
		};		
		IOException exn = iterNodes(printNode,null);
		if (exn != null)
			throw exn;
		EdgeFun<IOException> printEdge = new EdgeFun<IOException> () {
			public IOException run(Edge e, IOException exn) {
				if (exn == null) {
					try {
						w0.write(e.toString());
						w0.write(";\n");
					} catch (IOException exn0) {
						exn = exn0;
					}
				}
				return exn;
			}
		};		
		exn = iterEdges(printEdge,null);
		if (exn != null) 
			throw exn;
		w0.write("}\n");
		w0.flush();
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
}    
