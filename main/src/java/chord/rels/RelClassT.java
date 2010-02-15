/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.rels;

import joeq.Class.jq_Class;
import chord.project.Chord;
import chord.project.ProgramRel;
import chord.visitors.IClassVisitor;

/**
 * Relation containing each (concrete or abstract) class type
 * (as opposed to interface types, primitive types, etc.).
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Chord(
	name = "classT",
	sign = "T0"
)
public class RelClassT extends ProgramRel implements IClassVisitor {
	public void visit(jq_Class c) {
		if (!c.isInterface())
        	add(c);
	}
}