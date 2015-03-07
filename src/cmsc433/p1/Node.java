package cmsc433.p1;
/**
 * An <code>Node</code> is an object that represents a vertex in a
 * graph (and is used by classes that extend the <code>Graph</code>
 * abstract class).
 */
public class Node {
	private String name;
	public Node(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public String toString() {
		return name;
	}
	public boolean equals(Object o) {
		if (o instanceof Node) {
			Node n = (Node)o;
			return n.name.equals(this.name);
		}
		return false;
	}
	public int hashCode() {
		return name.hashCode();
	}
}
