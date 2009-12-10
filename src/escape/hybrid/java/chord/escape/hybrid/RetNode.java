/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.escape.hybrid;

import java.util.Set;

import chord.util.IntArraySet;
import chord.util.tuple.integer.IntTrio;

/**
 * @author Mayur Naik (mhn@cs.stanford.edu)
 *
 */
public class RetNode {
	final IntArraySet pts;
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
		return pts.equals(that.pts) &&
			esc.equals(that.esc) &&
			heap.equals(that.heap);
	}
	public String toString() {
		return "pts@r=" + ThreadEscapeFullAnalysis.toString(pts) +
			";heap@r=" + ThreadEscapeFullAnalysis.toString(heap) +
			";esc@r=" + ThreadEscapeFullAnalysis.toString(esc);
	}
}