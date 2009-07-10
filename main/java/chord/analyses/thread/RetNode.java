package chord.analyses.thread;

import java.util.Set;

import chord.util.CompareUtils;
import chord.util.IntArraySet;
import chord.util.tuple.integer.IntTrio;

public class RetNode {
	final IntArraySet pts;  // may be null (i.e. escPts)
	final Set<IntTrio> heap;
	final IntArraySet esc;
	public RetNode(IntArraySet p, Set<IntTrio> h,
			IntArraySet e) {
		pts = p;
		heap = h;
		esc = e;
	}
	public int hashCode() {
		return heap.hashCode();
	}
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof RetNode))
			return false;
		RetNode that = (RetNode) o;
		return CompareUtils.areEqual(pts, that.pts) &&
			esc.equals(that.esc) &&
			heap.equals(that.heap);
	}
	public String toString() {
		return "pts@r=" + HybridThreadEscapeAnalysis.toString(pts) +
			";heap@r=" + HybridThreadEscapeAnalysis.toString(heap) +
			";esc@r=" + HybridThreadEscapeAnalysis.toString(esc);
	}
}
