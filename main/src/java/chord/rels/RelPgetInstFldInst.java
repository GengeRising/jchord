/*
 * Copyright (c) 2008-2009, Intel Corporation.
 * Copyright (c) 2006-2007, The Trustees of Stanford University.
 * All rights reserved.
 */
package chord.rels;

import joeq.Class.jq_Class;
import joeq.Class.jq_Field;
import joeq.Class.jq_Method;
import joeq.Compiler.Quad.Operator;
import joeq.Compiler.Quad.Quad;
import joeq.Compiler.Quad.Operand.RegisterOperand;
import joeq.Compiler.Quad.Operand.FieldOperand;
import joeq.Compiler.Quad.Operator.ALoad;
import joeq.Compiler.Quad.Operator.Getfield;
import joeq.Compiler.Quad.RegisterFactory.Register;
import chord.doms.DomP;
import chord.doms.DomV;
import chord.doms.DomF;
import chord.project.Chord;
import chord.project.ProgramRel;
import chord.visitors.IHeapInstVisitor;

/**
 * Relation containing each tuple (m,b,f,v) such that the statement
 * at program point p is of the form <tt>b.f = v</tt>.
 *
 * @author Mayur Naik (mhn@cs.stanford.edu)
 */
@Chord(
	name = "PgetInstFldInst",
	sign = "P0,V0,F0,V1:F0_P0_V0xV1"
)
public class RelPgetInstFldInst extends ProgramRel
		implements IHeapInstVisitor {
	private DomP domP;
	private DomV domV;
	private DomF domF;
	public void init() {
		domP = (DomP) doms[0];
		domV = (DomV) doms[1];
		domF = (DomF) doms[2];
	}
	public void visit(jq_Class c) { }
	public void visit(jq_Method m) { }
	public void visitHeapInst(Quad q) {
		Operator op = q.getOperator();
		if (op instanceof ALoad) {
			if (((ALoad) op).getType().isReferenceType()) {
				RegisterOperand lo = ALoad.getDest(q);
				Register l = lo.getRegister();
				RegisterOperand bo = (RegisterOperand) ALoad.getBase(q);
				Register b = bo.getRegister();
				int pIdx = domP.indexOf(q);
				assert (pIdx != -1);
				int lIdx = domV.indexOf(l);
				assert (lIdx != -1);
				int bIdx = domV.indexOf(b);
				assert (bIdx != -1);
				int fIdx = 0;
				add(pIdx, lIdx, fIdx, bIdx);
			}
			return;
		}
		if (op instanceof Getfield) {
			FieldOperand fo = Getfield.getField(q);
			fo.resolve();
			jq_Field f = fo.getField();
			if (f.getType().isReferenceType()) {
				RegisterOperand lo = Getfield.getDest(q);
				Register l = lo.getRegister();
				RegisterOperand bo = (RegisterOperand) Getfield.getBase(q);
				Register b = bo.getRegister();
				int pIdx = domP.indexOf(q);
				assert (pIdx != -1);
				int lIdx = domV.indexOf(l);
				assert (lIdx != -1);
				int bIdx = domV.indexOf(b);
				assert (bIdx != -1);
				int fIdx = domF.indexOf(f);
				if (fIdx == -1) {
					System.out.println("WARNING: PgetInstFldInst: " +
						" quad: " + q);
				} else
					add(pIdx, lIdx, fIdx, bIdx);
			}
		}
	}
}