package cmsc433.p1;
import java.lang.Iterable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An <code>ImmutableList</code> is an immutable list of objects. That
 * is, once created, a list's elements can never change. The methods
 * in the <code>ImmutableList</code> class instead create new lists
 * that share structure with the list they start from.
 */
public class ImmutableList<E> implements Iterable<E> {
	/** 
	 * The element in this node of the list.
	 */
	public final E elem;
	/**
	 * The pointer to the remainder of the list. 
	 */
	public final ImmutableList<E> next;
	private int count;

	/**
	 * Constructs an immutable list from an element and
	 * another immutable list containing elements of the same type.
	 */
	public ImmutableList(E elem, ImmutableList<E> next) {
		this.elem = elem;
		this.next = next;
		if (next != null)
			this.count = next.count + 1;
		else
			this.count = 1;
	}

	/**
	 * Checks whether the given element is in this list.
	 *
	 * @param elem The element to look for in this list
	 * @return true if the element is present in the list
	 */
	public boolean member(E elem) {
		for (E aElem : this) {
			if (elem.equals(aElem)) return true;
		}
		return false;
	}

	/**
	 * Creates a new list from the current list, with the given element
	 * added to the front of it.
	 *
	 * @param elem The first element of the new list
	 * @return the new list with the given element at the front of it
	 * with the present list used as the rest
	 */
	public ImmutableList<E> add(E elem) {
		return new ImmutableList<E>(elem,this);
	}

	/**
	 * Creates a new list containing all elements from the present list
	 * but with the first occurrence of the given element removed.
	 *
	 * @param elem The element to remove from the list
	 * @return a list containing all the elements of the present list
	 * but with the first occurrence of the given element removed.
	 */
	public ImmutableList<E> remove(E elem) {
		if (this.elem.equals(elem)) return this.next;
		if (this.next == null) return this;
		else return new ImmutableList<E>(this.elem, this.next.remove(elem));
	}

	/**
	 * @return the size of the present list
	 */
	public int size() {
		return count;
	}

	/**
	 * @return an <code>Iterator</code> over the elements in the list.
	 */
	public Iterator<E> iterator () {
		final ImmutableList<E> me = this;
		return new Iterator<E>() {
			private ImmutableList<E> last = null;
			private ImmutableList<E> cur = me;
			public boolean hasNext() {
				return cur != null;
			}
			public E next() {
				if (cur != null) {
					last = cur;
					cur = cur.next;
					return last.elem;
				} else {
					throw new NoSuchElementException();
				}
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}