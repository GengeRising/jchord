/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.project;

import joeq.Compiler.Quad.ControlFlowGraph;

/**
 * 
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
public interface ICFGVisitor {
	public void visit(ControlFlowGraph cfg);
}