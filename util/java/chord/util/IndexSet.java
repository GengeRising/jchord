package chord.util;

import java.util.List;
import java.util.Set;

/**
 * Specification for indexing a set of objects by the order in which
 * the objects are added to the set.
 * <p>
 * The only mutating operation is {@link #add(Object)}, in particular,
 * objects cannot be removed.
 * <p>
 * Provides constant-time operations for adding a given object,
 * testing membership of a given object, and getting the object at a
 * given index.
 * 
 * @param	<T>	The type of objects in the set.
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public interface IndexSet<T> extends List<T>, Set<T> {

}
