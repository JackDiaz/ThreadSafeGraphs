package cmsc433.p1;
/**
 * <code>EdgeFun</code> objects represent functions that are called
 * for each edge in a graph by the <code>Graph.iterEdges</code>
 * function.
 */
public interface EdgeFun<A> {
	public A run(Edge edge, A accumulator);
}