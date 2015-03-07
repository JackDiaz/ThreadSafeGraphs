package cmsc433.p1;
/**
 * <code>NodeFun</code> objects represent functions that are called
 * for particular nodes in a graph by the <code>Graph.iterNodes</code>
 * function, and other iteration functions in that class.
 */
public interface NodeFun<A> {
	public A run(Node n, A accumulator);
}