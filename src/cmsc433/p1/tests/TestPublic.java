package cmsc433.p1.tests;
import static org.junit.Assert.assertTrue;
import cmsc433.p1.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

public class TestPublic extends TestCase
{
	private ArrayList<String> nodeNames = new ArrayList<String>();
	private ArrayList<String> smallNodeNames = new ArrayList<String>();
	private ArrayList<Edge> edgeList = new ArrayList<Edge>();
	
	public TestPublic() {
		for (int i = 1; i <= 100; ++i)
			nodeNames.add(Integer.toString(i));
		edgeList.add(Edge.create(new Node("A"), new Node("B")));
		edgeList.add(Edge.create(new Node("B"), new Node("C")));
		edgeList.add(Edge.create(new Node("C"), new Node("I")));
		edgeList.add(Edge.create(new Node("I"), new Node("A")));
		edgeList.add(Edge.create(new Node("A"), new Node("F")));
		edgeList.add(Edge.create(new Node("A"), new Node("G")));
		edgeList.add(Edge.create(new Node("G"), new Node("F")));
		edgeList.add(Edge.create(new Node("D"), new Node("G")));
		edgeList.add(Edge.create(new Node("D"), new Node("C")));
		edgeList.add(Edge.create(new Node("C"), new Node("E")));
		smallNodeNames.add("A");
		smallNodeNames.add("B");
		smallNodeNames.add("C");
		smallNodeNames.add("D");
		smallNodeNames.add("E");
		smallNodeNames.add("F");
		smallNodeNames.add("G");
		smallNodeNames.add("H");
		smallNodeNames.add("I");
	}
	
	private NodeFun<ArrayList<String>> getNodeNames = new NodeFun<ArrayList<String>>() {
		public ArrayList<String> run(Node n, ArrayList<String> accum) {
			accum.add(n.getName());
			return accum;
		}
	};
	
	private EdgeFun<ArrayList<Edge>> getEdges = new EdgeFun<ArrayList<Edge>>() {
		public ArrayList<Edge> run(Edge e, ArrayList<Edge> accum) {
			accum.add(e);
			return accum;
		}
	};
	
	private NodeFun<Integer> increment = new NodeFun<Integer> () {
		public Integer run(Node n, Integer count) {
		    return count+1;
		}
	};
	
	private <T> boolean hasSameElements(ArrayList<T> firstList, ArrayList<T> secondList) {
		if (secondList.size() != firstList.size())
			return false;
		
		for (T elem : firstList) {
			if (!secondList.contains(elem))
				return false;
		}
		
		return true;
	}
	
	private PersistentGraph getTestGraph() {
		PersistentGraph graph = PersistentGraph.emptyGraph();
		for (Edge e : edgeList)
			graph = graph.addEdge(e);
		
		graph = graph.addNode(new Node("H"));
		return graph;
	}
	
	private PersistentGraph buildGraph(String desc) {
		PersistentGraph graph = PersistentGraph.emptyGraph();
		String edges[] = desc.split(",");
		for (String edgeDesc : edges) {
			String nodeNames[] = edgeDesc.split(" ");
			graph = graph.addEdge(Edge.create(new Node(nodeNames[0]), new Node(nodeNames[1])));
		}
		
		return graph;
	}
	
	private boolean myEquals(PersistentGraph g1, PersistentGraph g2) {
		ArrayList<String> names1 = g1.iterNodes(getNodeNames, new ArrayList<String>());
		ArrayList<String> names2 = g2.iterNodes(getNodeNames, new ArrayList<String>());
		ArrayList<Edge> edges1 = g1.iterEdges(getEdges, new ArrayList<Edge>());
		ArrayList<Edge> edges2 = g2.iterEdges(getEdges, new ArrayList<Edge>());
		return (hasSameElements(names1, names2) && hasSameElements(edges1, edges2));
	}
	
	private boolean sanityCheck() {
		PersistentGraph graph = buildGraph("A B,B C");
		if (!myEquals(graph, graph))
			return false;
		
		PersistentGraph graph2 = buildGraph("D E");
		if (myEquals(graph, graph2))
			return false;
		
		return true;
	}

	@Test
	/** Test that we can add nodes to a graph
	 *  iterNodes must also work
	 */
	public void testAddNode() {
		PersistentGraph graph = PersistentGraph.emptyGraph();
		for (String name : nodeNames)
			graph = graph.addNode(new Node(name));
		
		ArrayList<String> actualNodeNames = graph.iterNodes(getNodeNames, new ArrayList<String>());
		assertTrue(hasSameElements(nodeNames, actualNodeNames));
	}
	
	@Test
	/** Test that we can add edges to a graph
	 *  iterEdges must also work
	 */
	public void testAddEdge() {
		PersistentGraph graph = getTestGraph();
		ArrayList<Edge> actualEdges = graph.iterEdges(getEdges, new ArrayList<Edge>());
		assertTrue(hasSameElements(edgeList, actualEdges));
	}
	
	@Test
	/** Test that we can remove nodes from a graph
	 * 
	 */
	public void testRemoveNode() {
		PersistentGraph graph = getTestGraph();
		ArrayList<String> actualNodeNames;
		ArrayList<Edge> actualEdges;
		graph = graph.removeNode(new Node("H"));
		smallNodeNames.remove("H");
		actualNodeNames = graph.iterNodes(getNodeNames, new ArrayList<String>());
		assertTrue(hasSameElements(smallNodeNames, actualNodeNames));
		actualEdges = graph.iterEdges(getEdges, new ArrayList<Edge>());
		assertTrue(hasSameElements(actualEdges, edgeList));
		graph = graph.removeNode(new Node("A"));
		smallNodeNames.remove("A");
		edgeList.remove(Edge.create(new Node("A"), new Node("B")));
		edgeList.remove(Edge.create(new Node("A"), new Node("F")));
		edgeList.remove(Edge.create(new Node("A"), new Node("G")));
		edgeList.remove(Edge.create(new Node("I"), new Node("A")));
		actualEdges = graph.iterEdges(getEdges, new ArrayList<Edge>());
		assertTrue(hasSameElements(actualEdges, edgeList));
	}
	
	@Test
	/** Test that we can remove edges from a graph
	 * 
	 */
	public void testRemoveEdge() {
		assertTrue(sanityCheck());
		PersistentGraph graph = buildGraph("A B,B C,C D");
		PersistentGraph answer = buildGraph("A B,C D");
		PersistentGraph actual = graph.removeEdge(Edge.create(new Node("B"), new Node("C")));
		assertTrue(myEquals(answer, actual));
		graph = buildGraph("A A,A B,B A,B C");
		answer = buildGraph("A B,B C");
		actual = graph.removeEdge(Edge.create(new Node("A"), new Node("A")));
		actual = actual.removeEdge(Edge.create(new Node("B"), new Node("A")));
		assertTrue(myEquals(answer, actual));
	}
	
	@Test
	/** Test iterSuccessors
	 * 
	 */
	public void testIterSuccessors() {
		PersistentGraph graph = buildGraph("A B,B C");
		ArrayList<String> expected = new ArrayList<String>(Arrays.asList("B"));
		assertTrue(hasSameElements(graph.iterSuccessors(new Node("A"), getNodeNames, new ArrayList<String>()), expected));
		graph = buildGraph("A B,B C,A D");
		expected = new ArrayList<String>(Arrays.asList("B", "D"));
		assertTrue(hasSameElements(graph.iterSuccessors(new Node("A"), getNodeNames, new ArrayList<String>()), expected));
		assertTrue(graph.iterSuccessors(new Node("B"), increment, 0).equals(1));
		graph = getTestGraph();
		expected = new ArrayList<String>(Arrays.asList("B", "F", "G"));
		assertTrue(hasSameElements(graph.iterSuccessors(new Node("A"), getNodeNames, new ArrayList<String>()), expected));
		graph = buildGraph("1 1,1 2,2 3");
		expected = new ArrayList<String>(Arrays.asList("1", "2"));
		assertTrue(hasSameElements(graph.iterSuccessors(new Node("1"), getNodeNames, new ArrayList<String>()), expected));
		assertTrue(graph.iterSuccessors(new Node("1"), increment, 0).equals(2));
	}
	
	@Test
	/** Test iterPredecessors
	 * 
	 */
	public void testIterPredecessors() {
		PersistentGraph graph = buildGraph("E F,F G");
		ArrayList<String> expected = new ArrayList<String>(Arrays.asList("F"));
		assertTrue(hasSameElements(graph.iterPredecessors(new Node("G"), getNodeNames, new ArrayList<String>()), expected));
		graph = buildGraph("E F,F G,A G");
		expected = new ArrayList<String>(Arrays.asList("F", "A"));
		assertTrue(hasSameElements(graph.iterPredecessors(new Node("G"), getNodeNames, new ArrayList<String>()), expected));
		graph = getTestGraph();
		expected = new ArrayList<String>(Arrays.asList("B", "D"));
		assertTrue(hasSameElements(graph.iterPredecessors(new Node("C"), getNodeNames, new ArrayList<String>()), expected));
		graph = buildGraph("1 1,1 2,2 3");
		expected = new ArrayList<String>(Arrays.asList("1"));
		assertTrue(hasSameElements(graph.iterPredecessors(new Node("1"), getNodeNames, new ArrayList<String>()), expected));
	}
	
	//ST TESTS
	public static void testPSTNodes()
	{
		//tests basic adding and removing of nodes
		int numNodes = 5;
		ImperativeGraph g = new ImperativeGraph();
		List<Node> nodes = createNodes(numNodes);
		for(Node n : nodes)
			g.addNode(n);
		TestCase.assertTrue(g.removeNode(nodes.get(0)));
		for(int i=1;i<numNodes;i++)
		{
			TestCase.assertTrue(g.removeNode(nodes.get(i)));
			TestCase.assertFalse(g.removeNode(nodes.get(i-1)));
		}
	}
	public static void testPSTEdges1()
	{
		//checks that nodes are added if needed when adding edges
		int numNodes = 5;
		ImperativeGraph g = new ImperativeGraph();
		List<Edge> edges = createEdgesPublic(numNodes);
		for(Edge e : edges)
			g.addEdge(e);

		for(Edge e : edges)
		{
			TestCase.assertTrue(g.removeEdge(e));
			TestCase.assertTrue(g.removeNode(e.getSource()));
		}
	}
	public static void testPSTEdges2()
	{
		//checks if related edges are removed when nodes are removed
		int numNodes = 5;
		ImperativeGraph g = new ImperativeGraph();
		List<Node> nodes = createNodes(numNodes);
		List<Edge> edges = createEdgesPublic(numNodes);
		for(Edge e : edges)
			g.addEdge(e);

		TestCase.assertTrue(g.removeNode(nodes.get(1)));
		TestCase.assertFalse(g.removeEdge(edges.get(0)));
		TestCase.assertFalse(g.removeEdge(edges.get(1)));
		TestCase.assertTrue(g.removeEdge(edges.get(2)));
		TestCase.assertTrue(g.removeNode(nodes.get(2)));

	}
	public static void testPSTEquals()
	{
		//checks the equals() method
		int numNodes = 5;
		ImperativeGraph g1 = new ImperativeGraph();
		ImperativeGraph g2 = new ImperativeGraph();
		List<Node> nodes = createNodes(numNodes);
		List<Edge> edges = createEdgesPublic(numNodes);
		for(Edge e : edges)
		{
			g1.addEdge(e);
			g2.addEdge(e);
		}
		TestCase.assertTrue(g1.equals(g2));
		g2.addEdge(Edge.create(nodes.get(0), nodes.get(numNodes-1)));
		TestCase.assertFalse(g1.equals(g2));
	}
	public static void testPSTCopy()
	{
		int numNodes = 7;
		ImperativeGraph g1 = new ImperativeGraph();
		List<Edge> edges = createEdgesPublic(numNodes);
		for(Edge e : edges)
			g1.addEdge(e);
		ImperativeGraph g2 = g1.copy();
		List<Node> n1 = g1.iterNodes(nf(), new ArrayList<Node>());
		List<Node> n2 = g2.iterNodes(nf(), new ArrayList<Node>());
		List<Edge> e1 = g1.iterEdges(ef(), new ArrayList<Edge>());
		List<Edge> e2 = g2.iterEdges(ef(), new ArrayList<Edge>());
		TestCase.assertTrue(n2.containsAll(n1));
		TestCase.assertTrue(e2.containsAll(e1));
		TestCase.assertTrue(n1.containsAll(n2));
		TestCase.assertTrue(e1.containsAll(e2));
		
		g1.removeNode(edges.get(0).getSource());
		

		n1 = g1.iterNodes(nf(), new ArrayList<Node>());
		n2 = g2.iterNodes(nf(), new ArrayList<Node>());
		e1 = g1.iterEdges(ef(), new ArrayList<Edge>());
		e2 = g2.iterEdges(ef(), new ArrayList<Edge>());
		
		TestCase.assertTrue(n2.containsAll(n1));
		TestCase.assertTrue(e2.containsAll(e1));
		TestCase.assertFalse(n1.containsAll(n2));
		TestCase.assertFalse(e1.containsAll(e2));
		
		g2.removeNode(edges.get(0).getSource());
		g2.addEdge(edges.get(0));
		g2.addEdge(edges.get(edges.size()-1));
		
		n1 = g1.iterNodes(nf(), new ArrayList<Node>());
		n2 = g2.iterNodes(nf(), new ArrayList<Node>());
		e1 = g1.iterEdges(ef(), new ArrayList<Edge>());
		e2 = g2.iterEdges(ef(), new ArrayList<Edge>());
		
		TestCase.assertTrue(n2.containsAll(n1));
		TestCase.assertTrue(e2.containsAll(e1));
		TestCase.assertFalse(n1.containsAll(n2));
		TestCase.assertFalse(e1.containsAll(e2));
		
		g2.removeNode(edges.get(0).getSource());
		n1 = g1.iterNodes(nf(), new ArrayList<Node>());
		n2 = g2.iterNodes(nf(), new ArrayList<Node>());
		e1 = g1.iterEdges(ef(), new ArrayList<Edge>());
		e2 = g2.iterEdges(ef(), new ArrayList<Edge>());
		
		TestCase.assertTrue(n2.containsAll(n1));
		TestCase.assertTrue(e2.containsAll(e1));
		TestCase.assertTrue(n1.containsAll(n2));
		TestCase.assertTrue(e1.containsAll(e2));
		
		g2.removeNode(edges.get(1).getSource());
		
		n1 = g1.iterNodes(nf(), new ArrayList<Node>());
		n2 = g2.iterNodes(nf(), new ArrayList<Node>());
		e1 = g1.iterEdges(ef(), new ArrayList<Edge>());
		e2 = g2.iterEdges(ef(), new ArrayList<Edge>());
		
		TestCase.assertFalse(n2.containsAll(n1));
		TestCase.assertFalse(e2.containsAll(e1));
		TestCase.assertTrue(n1.containsAll(n2));
		TestCase.assertTrue(e1.containsAll(e2));
	}
	public static void testPSTIter()
	{
		int numNodes = 7;
		ImperativeGraph g = new ImperativeGraph();
		List<Node> nodes = createNodes(numNodes);
		List<Edge> edges = createEdgesPublic(numNodes);

		for(Edge e : edges)
			g.addEdge(e);

		//check iterNodes
		List<Node> nodeList = g.iterNodes(nf(), new ArrayList<Node>());
		TestCase.assertTrue(nodeList.containsAll(nodes));

		//check iterEdges
		List<Edge> edgeList = g.iterEdges(ef(), new ArrayList<Edge>());
		TestCase.assertTrue(edgeList.containsAll(edges));

		//check iterSuccessors
		int split = (int)Math.floor(nodes.size()/2);
		List<Node> succList = g.iterSuccessors(nodes.get(split), nf(), new ArrayList<Node>());
		TestCase.assertTrue(succList.contains(nodes.get(split+1)));

		//check iterPredecessors
		List<Node> predList = g.iterPredecessors(nodes.get(split), nf(), new ArrayList<Node>());
		TestCase.assertTrue(predList.contains(nodes.get(split-1)));
	}
	public static void testPSTEmpty()
	{
		ImperativeGraph g = new ImperativeGraph();
		TestCase.assertTrue(g.isEmpty());
	}
	public static void testPSTDegree()
	{
		int numNodes = 5;
		ImperativeGraph g = new ImperativeGraph();
		List<Node> nodes = createNodes(numNodes);
		List<Edge> edges = createEdgesPublic(numNodes);
		for(Edge e : edges)
			g.addEdge(e);
		g.addEdge(Edge.create(nodes.get(2),nodes.get(1)));
		TestCase.assertEquals(1,g.outDegree(nodes.get(0)));
		TestCase.assertEquals(1,g.outDegree(nodes.get(1)));
		TestCase.assertEquals(2,g.outDegree(nodes.get(2)));
		TestCase.assertEquals(0,g.inDegree(nodes.get(0)));
		TestCase.assertEquals(2,g.inDegree(nodes.get(1)));
		TestCase.assertEquals(1,g.inDegree(nodes.get(2)));
	}
	public static void testPSTToPersistent()
	{
		//checks the toPersistentGraph() method
		int numNodes = 5;
		ImperativeGraph g1 = new ImperativeGraph();
		List<Edge> edges = createEdgesPublic(numNodes);
		for(Edge e : edges)
			g1.addEdge(e);
		PersistentGraph g2 = g1.toPersistentGraph();
		List<Node> n1 = g1.iterNodes(nf(), new ArrayList<Node>());
		List<Node> n2 = g2.iterNodes(nf(), new ArrayList<Node>());
		List<Edge> e1 = g1.iterEdges(ef(), new ArrayList<Edge>());
		List<Edge> e2 = g2.iterEdges(ef(), new ArrayList<Edge>());
		TestCase.assertTrue(n2.containsAll(n1));
		TestCase.assertTrue(e2.containsAll(e1));
	}
	 

	//HELPERS
	private static List<Node> createNodes(int n)
	{
		//creates n nodes
		List<Node> nodes = new ArrayList<Node>(n);
		for(int i=0;i<n;i++)
			nodes.add(new Node("Node"+i));
		return nodes;
	}
	private static List<Edge> createEdgesPublic(int n)//n : number of nodes
	{
		List<Edge> edges = new ArrayList<Edge>();
		List<Node> nodes = createNodes(n);
		for(int i=0;i<n-1;i++)
			edges.add(Edge.create(nodes.get(i), nodes.get(i+1)));
		return edges;
	}
	private static NodeFun<List<Node>> nf()
	{
		//function to return all the nodes in a list
		NodeFun<List<Node>> nf = new NodeFun<List<Node>>(){
			public List<Node> run(Node n, List<Node> l){
				l.add(n);
				return l;
			}};
			return nf;
	}
	private static EdgeFun<List<Edge>> ef()
	{
		//function to return all the edges in a list
		EdgeFun<List<Edge>> ef = new EdgeFun<List<Edge>>(){
			public List<Edge> run(Edge e, List<Edge> l){
				l.add(e);
				return l;
			}};
			return ef;
	}


}
