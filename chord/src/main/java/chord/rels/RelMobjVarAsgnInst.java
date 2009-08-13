/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.rels;

import joeq.Class.jq_Type;
import joeq.Class.jq_Class;
import joeq.Class.jq_Method;
import joeq.Compiler.Quad.Operand;
import joeq.Compiler.Quad.Quad;
import joeq.Compiler.Quad.Operand.ParamListOperand;
import joeq.Compiler.Quad.Operand.RegisterOperand;
import joeq.Compiler.Quad.Operator.Move;
import joeq.Compiler.Quad.Operator.Phi;
import joeq.Compiler.Quad.RegisterFactory.Register;

import chord.project.Chord;
import chord.project.ProgramRel;
import chord.visitors.IMoveInstVisitor;
import chord.visitors.IPhiInstVisitor;

/**
 * Relation containing each tuple (m,v1,v2) such that method m
 * contains a statement of the form <tt>v1 = v2</tt>.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Chord(
	name = "MobjVarAsgnInst",
	sign = "M0,V0,V1:M0_V0xV1"
)
public class RelMobjVarAsgnInst extends ProgramRel
		implements IMoveInstVisitor, IPhiInstVisitor {
	private jq_Method ctnrMethod;
	public void visit(jq_Class c) { }
	public void visit(jq_Method m) {
		ctnrMethod = m;
	}
	public void visitMoveInst(Quad q) {
		Operand rx = Move.getSrc(q);
		if (rx instanceof RegisterOperand) {
			RegisterOperand ro = (RegisterOperand) rx;
			if (ro.getType().isReferenceType()) {
				Register r = ro.getRegister();
				RegisterOperand lo = Move.getDest(q);
				Register l = lo.getRegister();
				add(ctnrMethod, l, r);
			}
		}
	}
	public void visitPhiInst(Quad q) {
		RegisterOperand lo = Phi.getDest(q);
		jq_Type t = lo.getType();
		if (t == null || t.isReferenceType()) {
			Register l = lo.getRegister();
			ParamListOperand ros = Phi.getSrcs(q);
			int n = ros.length();
			for (int i = 0; i < n; i++) {
				RegisterOperand ro = ros.get(i);
				if (ro != null) {
					Register r = ro.getRegister();
					add(ctnrMethod, l, r);
				}
			}
		}
	}
}